package com.supplychainx.delivery_service.service.impl;

import com.supplychainx.delivery_service.dto.request.OrderRequest;
import com.supplychainx.delivery_service.dto.response.OrderResponse;
import com.supplychainx.delivery_service.mapper.OrderMapper;
import com.supplychainx.delivery_service.model.Customer;
import com.supplychainx.delivery_service.model.Order;
import com.supplychainx.delivery_service.repository.CustomerRepository;
import com.supplychainx.delivery_service.repository.OrderRepository;
import com.supplychainx.delivery_service.service.OrderService;
import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.production_service.dto.request.ProductionOrderRequest;
import com.supplychainx.production_service.model.Product;
import com.supplychainx.production_service.model.ProductionOrder;
import com.supplychainx.production_service.repository.ProductRepository;
import com.supplychainx.production_service.service.ProductionOrderService;
import com.supplychainx.supply_service.model.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final ProductionOrderService productionOrderService;

    /**
     * Helper method to retrieve an Order entity or throw an exception.
     */
    private Order findOrderEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Order not found with ID: " + id));
    }

    /**
     * Helper method to retrieve a Customer entity or throw an exception.
     */
    private Customer findCustomerEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Customer not found with ID: " + id));
    }

    /**
     * Helper method to retrieve a Product entity or throw an exception.
     */
    private Product findProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with ID: " + id));
    }


    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Customer customer = findCustomerEntity(request.getCustomerId());
        Product product = findProductEntity(request.getProductId());



        if (product.getStock() < request.getQuantity()) {
//            throw new IllegalStateException("Cannot create order: Insufficient stock for product " + product.getName() +
//                    ". Available: " + product.getStock() + ", Requested: " + request.getQuantity());

            //creating the object of production order, so we can automatically create a production order when the stock is inssufisant with the quantity the client is demanding
            //ProductionOrderRequest  productionOrderRequest =  new ProductionOrderRequest(product.getId(),request.getQuantity()-product.getStock());

            //you can use one of the two ways to create the production order automatically, below directly we are using the objec we created above this line
            //productionOrderService.createOrder(productionOrderRequest);

            //or you can use the way were you use the builder
            productionOrderService.createOrder(ProductionOrderRequest.builder().productId(product.getId()).quantity(request.getQuantity()-product.getStock()).build());
        }

        Order newOrder = orderMapper.toEntity(request);
        newOrder.setCustomer(customer);
        product.setStock(product.getStock() - request.getQuantity());
        newOrder.setProduct(product);



        // Status and orderDate are set via @PrePersist in the Order entity (default: IN_PREPARATION)

        Order savedOrder = orderRepository.save(newOrder);
        return orderMapper.toResponse(savedOrder);
    }


    @Override
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        Order orderToUpdate = findOrderEntity(id);

        // Disallow modification if the order is already shipped or delivered
        if (orderToUpdate.getStatus() != OrderStatus.IN_PREPARATION) {
            throw new IllegalStateException("Order cannot be modified as its status is not IN_PREPARATION.");
        }

        // Check new product stock if quantity changed
        Product newProduct = findProductEntity(request.getProductId());
        if (newProduct.getStock() < request.getQuantity()) {
            throw new IllegalStateException("Cannot update order: Insufficient stock for product " + newProduct.getName() +
                    ". Available: " + newProduct.getStock() + ", Requested: " + request.getQuantity());
        }


        orderToUpdate.setQuantity(request.getQuantity());


        if (!orderToUpdate.getCustomer().getId().equals(request.getCustomerId())) {
            orderToUpdate.setCustomer(findCustomerEntity(request.getCustomerId()));
        }
        if (!orderToUpdate.getProduct().getId().equals(request.getProductId())) {
            orderToUpdate.setProduct(newProduct);
        }

        Order updatedOrder = orderRepository.save(orderToUpdate);
        return orderMapper.toResponse(updatedOrder);
    }


    @Override
    public OrderResponse cancelOrder(Long id) {
        Order order = findOrderEntity(id);


        if (order.getStatus() != OrderStatus.IN_PREPARATION) {
            throw new IllegalStateException("Order can only be canceled if its status is IN_PREPARATION.");
        }

        // 🔑 Use the new CANCELED status
        order.setStatus(OrderStatus.CANCELED);

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return orderMapper.toResponse(findOrderEntity(id));
    }

    // --- Internal Status Update Methods (Delivery Supervisor) ---

    @Override
    public OrderResponse shipOrder(Long id) {
        Order order = findOrderEntity(id);

        if (order.getStatus() != OrderStatus.IN_PREPARATION) {
            throw new IllegalStateException("Cannot ship order. Status must be IN_PREPARATION.");
        }

        order.setStatus(OrderStatus.IN_TRANSIT);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse completeOrder(Long id) {
        Order order = findOrderEntity(id);

        if (order.getStatus() != OrderStatus.IN_TRANSIT) {
            throw new IllegalStateException("Cannot complete order. Status must be IN_TRANSIT.");
        }

        // 🔑 Stock deduction logic
        Product product = order.getProduct();
        int newStock = product.getStock() - order.getQuantity();

        if (newStock < 0) {
            throw new IllegalStateException("Critical Error: Stock dropped below zero after order completion.");
        }

        product.setStock(newStock);
        productRepository.save(product);

        order.setStatus(OrderStatus.DELIVERED);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {

    }
}