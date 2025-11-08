package com.supplychainx.delivery_service.controller;

import com.supplychainx.delivery_service.dto.request.OrderRequest;
import com.supplychainx.delivery_service.dto.response.OrderResponse;
import com.supplychainx.delivery_service.service.OrderService;
import com.supplychainx.supply_service.model.enums.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Helper endpoint to get a single order by ID (often useful for debugging/testing)
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Consult the list of all orders
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> response = orderService.getAllOrders();
        return ResponseEntity.ok(response);
    }

    // Follow the status of orders (Filter by status)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> response = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(response);
    }

    // Create a customer order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Modify an existing order
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.updateOrder(id, request);
        return ResponseEntity.ok(response);
    }

    // Cancel an order if non expédiée
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        OrderResponse response = orderService.cancelOrder(id);
        return ResponseEntity.ok(response);
    }

    // --- Internal Delivery Supervisor Actions (Status Updates) ---

    @PostMapping("/{id}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable Long id) {
        OrderResponse response = orderService.shipOrder(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long id) {
        OrderResponse response = orderService.completeOrder(id);
        return ResponseEntity.ok(response);
    }
}