package com.supplychainx.production_service.controller;

import com.supplychainx.production_service.dto.request.ProductionOrderRequest;
import com.supplychainx.production_service.dto.response.ProductionOrderResponse;
import com.supplychainx.production_service.service.ProductionOrderService;
import com.supplychainx.supply_service.model.enums.ProductionStatus; // Using the status enum path from your service
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/production/orders")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    // US28 & US29: Create a new order (Planner role)
    @PostMapping
    public ResponseEntity<ProductionOrderResponse> createOrder(@Valid @RequestBody ProductionOrderRequest request) {
        ProductionOrderResponse response = productionOrderService.createOrder(request);

        // Check if the order was blocked due to material shortage during creation
        if (response.getStatus() == ProductionStatus.BLOCKED) {
            // Returns 202 Accepted but with a BLOCKED status warning in the body
            return ResponseEntity.accepted().body(response);
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        // Returns 201 Created for PENDING/successful orders
        return ResponseEntity.created(location).body(response);
    }

    // US26: Consult the list of all orders
    @GetMapping
    public ResponseEntity<List<ProductionOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(productionOrderService.getAllOrders());
    }

    // Standard GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(productionOrderService.getOrderById(id));
    }

    // US27: Consult the status of orders by filtering (Supervisor role)
    @GetMapping("/status")
    public ResponseEntity<List<ProductionOrderResponse>> getOrdersByStatus(@RequestParam ProductionStatus status) {
        return ResponseEntity.ok(productionOrderService.getOrdersByStatus(status));
    }

    // --- Status Update Endpoints (Supervisor Actions) ---

    // Transition: PENDING -> IN_PRODUCTION (Start Order)
    @PutMapping("/{id}/start")
    public ResponseEntity<ProductionOrderResponse> startOrder(@PathVariable Long id) {
        ProductionOrderResponse response = productionOrderService.startOrder(id);
        return ResponseEntity.ok(response);
    }

    // Transition: IN_PRODUCTION -> COMPLETED (Complete Order)
    @PutMapping("/{id}/complete")
    public ResponseEntity<ProductionOrderResponse> completeOrder(@PathVariable Long id) {
        ProductionOrderResponse response = productionOrderService.completeOrder(id);
        return ResponseEntity.ok(response);
    }

    // US26: Cancel an order (Must be PENDING)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ProductionOrderResponse> cancelOrder(@PathVariable Long id) {
        ProductionOrderResponse response = productionOrderService.cancelOrder(id);
        return ResponseEntity.ok(response);
    }

    // Manual Block Endpoint (Can be used for exceptional cases)
    @PutMapping("/{id}/block")
    public ResponseEntity<ProductionOrderResponse> blockOrder(@PathVariable Long id) {
        ProductionOrderResponse response = productionOrderService.blockOrder(id);
        return ResponseEntity.ok(response);
    }
}