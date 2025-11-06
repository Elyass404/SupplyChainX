package com.supplychainx.production_service.service.impl;

import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.supply_service.model.RawMaterial;
import com.supplychainx.production_service.dto.request.ProductionOrderRequest;
import com.supplychainx.production_service.dto.response.ProductionOrderResponse;
import com.supplychainx.production_service.mapper.ProductionOrderMapper;
import com.supplychainx.production_service.model.BillOfMaterial;
import com.supplychainx.production_service.model.Product;
import com.supplychainx.production_service.model.ProductionOrder;
import com.supplychainx.supply_service.model.enums.ProductionStatus;
import com.supplychainx.production_service.repository.BillOfMaterialRepository;
import com.supplychainx.production_service.repository.ProductRepository;
import com.supplychainx.production_service.repository.ProductionOrderRepository;
import com.supplychainx.production_service.service.ProductionOrderService;
import com.supplychainx.supply_service.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final BillOfMaterialRepository bomRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductionOrderMapper orderMapper;

    /**
     * US28 & US29: Creates a new order, checks material availability, and calculates estimated time.
     */
    @Override
    public ProductionOrderResponse createOrder(ProductionOrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with ID: " + request.getProductId()));

        ProductionOrder newOrder = ProductionOrder.builder()
                .product(product)
                .quantity(request.getQuantity())
                // Status is set to PENDING in @PrePersist hook
                .build();

        // --- 1. Perform planning and availability checks (US28 & US29) ---

        // Calculate estimated production time and end date (US29)
        calculatePlanning(newOrder, product);

        // Check material availability (US28)
        if (!isMaterialAvailable(newOrder)) {
            newOrder.setStatus(ProductionStatus.BLOCKED);
        }

        ProductionOrder savedOrder = orderRepository.save(newOrder);
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Helper method to calculate and set the estimated production time and end date (US29).
     * This uses the 'productionTime' field from the Product entity.
     */
    private void calculatePlanning(ProductionOrder order, Product product) {
        // Total Time = Product Production Time (in hours) * Requested Quantity
        double totalTime = (double) product.getProductionTime() * order.getQuantity();

        // Since the entity only has startDate and endDate, we'll calculate endDate here.
        // Assume 8 production hours per day.
        int requiredDays = (int) Math.ceil(totalTime / 8.0);

        // Set a default planning start date (e.g., tomorrow) and calculate the end date
        order.setStartDate(LocalDate.now().plusDays(1));
        order.setEndDate(order.getStartDate().plusDays(requiredDays));
    }

    /**
     * Helper method to verify stock for all required materials (US28).
     */
    private boolean isMaterialAvailable(ProductionOrder order) {
        List<BillOfMaterial> boms = bomRepository.findByProductId(order.getProduct().getId());

        if (boms.isEmpty()) {
            return true;
        }

        for (BillOfMaterial bom : boms) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(bom.getRawMaterial().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Raw Material in BOM not found."));

            double totalRequired = bom.getRequiredQuantity() * order.getQuantity();

            // US28: Compare required quantity vs. current stock (Using your 'stock' field)
            if (rawMaterial.getStock() < totalRequired) {
                return false;
            }
        }
        return true;
    }

    // --- Standard Read Operations (US26 & US27) ---

    @Override
    @Transactional(readOnly = true)
    public List<ProductionOrderResponse> getAllOrders() {
        return orderMapper.toResponseList(orderRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionOrderResponse getOrderById(Long id) {
        ProductionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Production Order not found with ID: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductionOrderResponse> getOrdersByStatus(ProductionStatus status) {
        return orderMapper.toResponseList(orderRepository.findByStatus(status));
    }

    // --- Status Update Operations (US26, US27) ---

    /**
     * US26: Cancel an order if it has not been started (must be PENDING).
     */
    @Override
    public ProductionOrderResponse cancelOrder(Long id) {
        ProductionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Production Order not found with ID: " + id));

        if (order.getStatus() != ProductionStatus.PENDING) {
            throw new IllegalStateException("Order can only be cancelled if status is PENDING. Current status: " + order.getStatus());
        }

        order.setStatus(ProductionStatus.BLOCKED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    /**
     * Business Logic: Move order to IN_PROGRESS. This must deduct material stock.
     */
    @Override
    public ProductionOrderResponse startOrder(Long id) {
        ProductionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Production Order not found with ID: " + id));

        // Ensure status allows starting
        if (order.getStatus() != ProductionStatus.PENDING) {
            throw new IllegalStateException("Order must be PENDING to be started. Current status: " + order.getStatus());
        }

        // Final check for materials before deduction
        if (!isMaterialAvailable(order)) {
            order.setStatus(ProductionStatus.BLOCKED);
            orderRepository.save(order);
            throw new IllegalStateException("Material check failed: Order status updated to BLOCKED.");
        }

        // Deduct materials from stock
        deductMaterialsFromStock(order);

        order.setStartDate(LocalDate.now());
        order.setStatus(ProductionStatus.IN_PRODUCTION);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    /**
     * Business Logic: Move order to COMPLETED. This must update the stock of the finished product.
     */
    @Override
    public ProductionOrderResponse completeOrder(Long id) {
        ProductionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Production Order not found with ID: " + id));

        if (order.getStatus() != ProductionStatus.IN_PRODUCTION) {
            throw new IllegalStateException("Order can only be completed if status is IN_PROGRESS. Current status: " + order.getStatus());
        }

        // Update finished product stock
        updateProductStock(order);

        order.setEndDate(LocalDate.now()); // Set actual completion date
        order.setStatus(ProductionStatus.COMPLETED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    /**
     * Internal logic for deducting materials from the Approvisionnement stock.
     */
    private void deductMaterialsFromStock(ProductionOrder order) {
        List<BillOfMaterial> boms = bomRepository.findByProductId(order.getProduct().getId());

        for (BillOfMaterial bom : boms) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(bom.getRawMaterial().getId()).get();
            double totalRequired = bom.getRequiredQuantity() * order.getQuantity();

            // Use your specific 'stock' field and integer type
            rawMaterial.setStock((int) (rawMaterial.getStock() - totalRequired));
            rawMaterialRepository.save(rawMaterial);
        }
    }

    /**
     * Internal logic for increasing the finished product stock.
     */
    private void updateProductStock(ProductionOrder order) {
        Product product = order.getProduct();

        product.setStock(product.getStock() + order.getQuantity());
        productRepository.save(product);
    }

    // Helper method to set status to BLOCKED if material check fails (primarily handled in createOrder)
    @Override
    public ProductionOrderResponse blockOrder(Long id) {
        ProductionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Production Order not found with ID: " + id));

        order.setStatus(ProductionStatus.BLOCKED);
        return orderMapper.toResponse(orderRepository.save(order));
    }
}