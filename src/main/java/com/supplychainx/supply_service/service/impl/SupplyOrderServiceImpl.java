package com.supplychainx.supply_service.service.impl;

import com.supplychainx.supply_service.dto.request.SupplyOrderMaterialRequest;
import com.supplychainx.supply_service.dto.request.SupplyOrderRequest;
import com.supplychainx.supply_service.dto.response.SupplyOrderResponse;
import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.supply_service.mapper.SupplyOrderMapper;
import com.supplychainx.supply_service.model.RawMaterial;
import com.supplychainx.supply_service.model.Supplier;
import com.supplychainx.supply_service.model.SupplyOrder;
import com.supplychainx.supply_service.model.SupplyOrderMaterial;
import com.supplychainx.supply_service.model.enums.SupplyOrderStatus;
import com.supplychainx.supply_service.repository.RawMaterialRepository;
import com.supplychainx.supply_service.repository.SupplierRepository;
import com.supplychainx.supply_service.repository.SupplyOrderMaterialRepository;
import com.supplychainx.supply_service.repository.SupplyOrderRepository;
import com.supplychainx.supply_service.service.SupplyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplyOrderServiceImpl implements SupplyOrderService {

    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplierRepository supplierRepository; // Added for validation
    private final RawMaterialRepository rawMaterialRepository; // Added for validation
    private final SupplyOrderMaterialRepository orderMaterialRepository; // Added for line item saving
    private final SupplyOrderMapper supplyOrderMapper;

    // --- Helper Methods ---

    /**
     * Finds and validates the Supplier entity based on ID.
     */
    private Supplier findValidSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RessourceNotFoundException("Supplier with ID: " + supplierId + " not found."));
    }

    /**
     * Creates and returns SupplyOrderMaterial entities from the request DTOs.
     * Validates material existence and checks for duplicate materials in the order list.
     */
    private List<SupplyOrderMaterial> createMaterialEntities(SupplyOrder order, List<SupplyOrderMaterialRequest> materialRequests) {
        // 1. Check for duplicate materials in the current request list
        Set<Long> uniqueMaterialIds = materialRequests.stream()
                .map(SupplyOrderMaterialRequest::getRawMaterialId)
                .collect(Collectors.toSet());

        if (uniqueMaterialIds.size() != materialRequests.size()) {
            throw new IllegalArgumentException("Order request contains duplicate raw materials.");
        }

        // 2. Fetch all required RawMaterial entities
        List<RawMaterial> materials = rawMaterialRepository.findAllById(uniqueMaterialIds);

        if (materials.size() != uniqueMaterialIds.size()) {
            // Find which IDs are missing for better error reporting
            Set<Long> foundIds = materials.stream().map(RawMaterial::getId).collect(Collectors.toSet());
            String missingIds = uniqueMaterialIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));

            throw new RessourceNotFoundException("One or more Raw Material IDs not found: " + missingIds);
        }

        // 3. Create SupplyOrderMaterial entities
        return materialRequests.stream().map(request -> {
            RawMaterial material = materials.stream()
                    .filter(m -> m.getId().equals(request.getRawMaterialId()))
                    .findFirst().get(); // Guaranteed to exist by previous check

            return SupplyOrderMaterial.builder()
                    .supplyOrder(order)
                    .rawMaterial(material)
                    .quantity(request.getQuantity())
                    .build();
        }).collect(Collectors.toList());
    }

    // --- CRUD Operations ---

    @Override
    public SupplyOrderResponse createSupplyOrder(SupplyOrderRequest request) {
        // 1. Uniqueness check (Supplier and Date)
        if (supplyOrderRepository.existsBySupplier_IdAndOrderDate(
                request.getSupplierId(),
                request.getOrderDate())) {
            throw new IllegalArgumentException("An order already exists for supplier ID " + request.getSupplierId() +
                    " on date " + request.getOrderDate());
        }

        // 2. Find and validate Supplier
        Supplier supplier = findValidSupplier(request.getSupplierId());

        // 3. Convert DTO to Entity and set simple fields/relationships
        SupplyOrder newOrder = supplyOrderMapper.toEntity(request);
        newOrder.setSupplier(supplier);
        newOrder.setStatus(SupplyOrderStatus.PENDING); // Set default status

        // 4. Save the parent entity first to get the ID
        SupplyOrder savedOrder = supplyOrderRepository.save(newOrder);

        // 5. Create and link SupplyOrderMaterial entities
        List<SupplyOrderMaterial> materialLineItems = createMaterialEntities(savedOrder, request.getMaterials());
        savedOrder.setMaterials(materialLineItems); // Link back to parent

        // 6. Save line items (if using cascade, this might be optional, but explicit save is safer for ManyToMany/OneToMany inserts)
        orderMaterialRepository.saveAll(materialLineItems);

        // 7. Convert and return Response DTO
        return supplyOrderMapper.toResponse(savedOrder);
    }

    @Override
    public SupplyOrderResponse updateSupplyOrder(Long id, SupplyOrderRequest request) {
        SupplyOrder existingOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Supply Order not found with ID: " + id));

        // 1. Find and validate Supplier if ID changed
        if (!existingOrder.getSupplier().getId().equals(request.getSupplierId())) {
            Supplier newSupplier = findValidSupplier(request.getSupplierId());
            existingOrder.setSupplier(newSupplier);
        }

        // 2. Update simple fields
        existingOrder.setOrderDate(request.getOrderDate());

        // 3. Handle status change if needed (not in Request DTO, so we skip for now)

        // 4. Update Materials (Complex: Delete existing line items and insert new ones)
        // a. Delete existing line items
        orderMaterialRepository.deleteAll(existingOrder.getMaterials());

        // b. Create new line items
        List<SupplyOrderMaterial> newMaterialLineItems = createMaterialEntities(existingOrder, request.getMaterials());
        existingOrder.setMaterials(newMaterialLineItems);

        // 5. Save the updated parent and new line items
        SupplyOrder updatedOrder = supplyOrderRepository.save(existingOrder);
        orderMaterialRepository.saveAll(newMaterialLineItems);

        // 6. Convert and return Response DTO
        return supplyOrderMapper.toResponse(updatedOrder);
    }

    @Override
    public void deleteSupplyOrder(Long id) {
        if (!supplyOrderRepository.existsById(id)) {
            throw new RessourceNotFoundException("Supply Order not found with ID: " + id);
        }
        supplyOrderRepository.deleteById(id);
    }

    // --- Retrieval & Query Methods ---

    @Override
    @Transactional(readOnly = true)
    public SupplyOrderResponse getSupplyOrderById(Long id) {
        SupplyOrder order = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Supply Order not found with ID: " + id));
        return supplyOrderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplyOrderResponse> getAllSupplyOrders() {
        return supplyOrderMapper.toResponseList(supplyOrderRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplyOrderResponse> getOrdersBySupplier(Long supplierId) {
        return supplyOrderMapper.toResponseList(supplyOrderRepository.findBySupplier_Id(supplierId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplyOrderResponse> getOrdersByStatus(SupplyOrderStatus status) {
        return supplyOrderMapper.toResponseList(supplyOrderRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplyOrderResponse> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        return supplyOrderMapper.toResponseList(supplyOrderRepository.findByOrderDateBetween(startDate, endDate));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySupplierAndDate(Long supplierId, LocalDate orderDate) {
        return supplyOrderRepository.existsBySupplier_IdAndOrderDate(supplierId, orderDate);
    }

    /**
     * Business Logic: Updates the order status to RECEIVED and increases RawMaterial stock.
     */
    @Override
    public SupplyOrderResponse receiveOrder(Long id) {
        SupplyOrder order = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Supply Order not found with ID: " + id));

        if (order.getStatus() == SupplyOrderStatus.RECEIVED) {
            throw new IllegalStateException("Order is already marked as RECEIVED.");
        }

        // 1. Perform stock increase
        increaseMaterialStock(order);

        // 2. Update status and save
        order.setStatus(SupplyOrderStatus.RECEIVED);
        SupplyOrder savedOrder = supplyOrderRepository.save(order);

        return supplyOrderMapper.toResponse(savedOrder);
    }

    /**
     * Internal logic for increasing materials in the RawMaterial stock.
     */
    private void increaseMaterialStock(SupplyOrder order) {
        // Note: We use the list of materials linked to the SupplyOrder entity
        List<SupplyOrderMaterial> lineItems = order.getMaterials();

        for (SupplyOrderMaterial item : lineItems) {
            // We assume rawMaterial is eagerly fetched or retrieved via proxy
            RawMaterial material = rawMaterialRepository.findById(item.getRawMaterial().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Raw Material in order line not found."));

            int receivedQuantity = item.getQuantity();

            // 🔑 INCREASE STOCK
            material.setStock(material.getStock() + receivedQuantity);

            rawMaterialRepository.save(material);
        }
    }
}