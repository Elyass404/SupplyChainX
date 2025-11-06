package com.supplychainx.supply_service.controller;

import com.supplychainx.supply_service.dto.request.SupplierRequest;
import com.supplychainx.supply_service.dto.response.SupplierResponse;
import com.supplychainx.supply_service.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    // --- 1. CREATE Supplier (POST) ---
    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.createSupplier(request);
        // Returns 201 Created and the created resource
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- 2. RETRIEVE All Suppliers (GET) ---
    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<SupplierResponse> suppliers = supplierService.getAllSuppliers();
        // Returns 200 OK (even if the list is empty)
        return ResponseEntity.ok(suppliers);
    }

    // --- 3. RETRIEVE Supplier by ID (GET) ---
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long id) {
        // The service layer uses orElseThrow(), so if the supplier is not found,
        // a 404 Not Found response is automatically returned by Spring's exception handler.
        SupplierResponse response = supplierService.getSupplierById(id);
        return ResponseEntity.ok(response);
    }

    // --- 4. UPDATE Supplier (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.updateSupplier(id, request);
        // Returns 200 OK and the updated resource
        return ResponseEntity.ok(response);
    }

    // --- 5. DELETE Supplier (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        // Returns 204 No Content upon successful deletion
        return ResponseEntity.noContent().build();
    }

    // --- 6. Business Query: Search by Name Fragment (GET) ---
    // Example: /api/v1/suppliers/search?query=ACME
    @GetMapping("/search")
    public ResponseEntity<List<SupplierResponse>> searchSuppliers(@RequestParam String query) {
        List<SupplierResponse> suppliers = supplierService.searchSuppliers(query);
        return ResponseEntity.ok(suppliers);
    }

    // --- 7. Business Query: High Rated Suppliers (GET) ---
    // Example: /api/v1/suppliers/high-rated?minRating=4.5
    @GetMapping("/high-rated")
    public ResponseEntity<List<SupplierResponse>> getHighRatedSuppliers(@RequestParam Double minRating) {
        List<SupplierResponse> suppliers = supplierService.getHighRatedSuppliers(minRating);
        return ResponseEntity.ok(suppliers);
    }
}