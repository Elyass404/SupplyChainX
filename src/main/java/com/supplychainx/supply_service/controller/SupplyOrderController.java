package com.supplychainx.supply_service.controller;

import com.supplychainx.supply_service.dto.request.SupplyOrderRequest;
import com.supplychainx.supply_service.dto.response.SupplyOrderResponse;
import com.supplychainx.supply_service.model.enums.SupplyOrderStatus;
import com.supplychainx.supply_service.service.SupplyOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/supply-orders")
@RequiredArgsConstructor
public class SupplyOrderController {

    private final SupplyOrderService supplyOrderService;

    // --- 1. CREATE Supply Order (POST) ---
    @PostMapping
    public ResponseEntity<SupplyOrderResponse> createSupplyOrder(@Valid @RequestBody SupplyOrderRequest request) {
        SupplyOrderResponse response = supplyOrderService.createSupplyOrder(request);
        // Returns 201 Created
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- 2. RETRIEVE All Orders (GET) ---
    @GetMapping
    public ResponseEntity<List<SupplyOrderResponse>> getAllSupplyOrders() {
        List<SupplyOrderResponse> orders = supplyOrderService.getAllSupplyOrders();
        // Returns 200 OK
        return ResponseEntity.ok(orders);
    }

    // --- 3. RETRIEVE Order by ID (GET) ---
    @GetMapping("/{id}")
    public ResponseEntity<SupplyOrderResponse> getSupplyOrderById(@PathVariable Long id) {
        // Service method throws 404/RessourceNotFoundException if not found
        SupplyOrderResponse response = supplyOrderService.getSupplyOrderById(id);
        return ResponseEntity.ok(response);
    }

    // --- 4. UPDATE Order (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<SupplyOrderResponse> updateSupplyOrder(
            @PathVariable Long id,
            @Valid @RequestBody SupplyOrderRequest request) {
        SupplyOrderResponse response = supplyOrderService.updateSupplyOrder(id, request);
        // Returns 200 OK
        return ResponseEntity.ok(response);
    }

    // --- 5. DELETE Order (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplyOrder(@PathVariable Long id) {
        supplyOrderService.deleteSupplyOrder(id);
        // Returns 204 No Content
        return ResponseEntity.noContent().build();
    }

    // --- 6. Query: Orders by Supplier ID (GET) ---
    // Example: /api/v1/supply-orders/by-supplier/1
    @GetMapping("/by-supplier/{supplierId}")
    public ResponseEntity<List<SupplyOrderResponse>> getOrdersBySupplier(@PathVariable Long supplierId) {
        List<SupplyOrderResponse> orders = supplyOrderService.getOrdersBySupplier(supplierId);
        return ResponseEntity.ok(orders);
    }

    // --- 7. Query: Orders by Status (GET) ---
    // Example: /api/v1/supply-orders/by-status?status=PENDING
    @GetMapping("/by-status")
    public ResponseEntity<List<SupplyOrderResponse>> getOrdersByStatus(@RequestParam SupplyOrderStatus status) {
        List<SupplyOrderResponse> orders = supplyOrderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // --- 8. Query: Orders by Date Range (GET) ---
    // Example: /api/v1/supply-orders/by-date?start=2025-01-01&end=2025-03-31
    @GetMapping("/by-date")
    public ResponseEntity<List<SupplyOrderResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<SupplyOrderResponse> orders = supplyOrderService.getOrdersByDateRange(start, end);
        return ResponseEntity.ok(orders);
    }

    // --- Status Update: Receive Order ---
// Endpoint: PUT /api/v1/supply-orders/{id}/receive
    @PutMapping("/{id}/receive")
    public ResponseEntity<SupplyOrderResponse> receiveOrder(@PathVariable Long id) {
        SupplyOrderResponse response = supplyOrderService.receiveOrder(id);
        // Returns 200 OK and the updated resource
        return ResponseEntity.ok(response);
    }
}