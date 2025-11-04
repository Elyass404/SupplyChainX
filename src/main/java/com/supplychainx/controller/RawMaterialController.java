package com.supplychainx.controller;

import com.supplychainx.dto.request.RawMaterialRequest;
import com.supplychainx.dto.response.RawMaterialResponse;
import com.supplychainx.service.RawMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    // --- 1. CREATE Raw Material (POST) ---
    @PostMapping
    public ResponseEntity<?> createRawMaterial(@Valid @RequestBody RawMaterialRequest request, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            Map<String, String> errors =  new HashMap<>();
            for(FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        RawMaterialResponse response = rawMaterialService.createRawMaterial(request);
        // Returns 201 Created
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- 2. RETRIEVE All Raw Materials (GET) ---
    @GetMapping
    public ResponseEntity<List<RawMaterialResponse>> getAllRawMaterials() {
        List<RawMaterialResponse> materials = rawMaterialService.findAllRawMaterial();
        // Returns 200 OK
        return ResponseEntity.ok(materials);
    }

    // --- 3. RETRIEVE Raw Material by ID (GET) ---
    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialResponse> getRawMaterialById(@PathVariable Long id) {
        // Service method throws 404/RessourceNotFoundException if not found
        RawMaterialResponse response = rawMaterialService.findRawMaterialById(id);
        return ResponseEntity.ok(response);
    }

    // --- 4. UPDATE Raw Material (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialResponse> updateRawMaterial(
            @PathVariable Long id,
            @Valid @RequestBody RawMaterialRequest request) {
        RawMaterialResponse response = rawMaterialService.updateRawMaterial(id, request);
        // Returns 200 OK
        return ResponseEntity.ok(response);
    }

    // --- 5. DELETE Raw Material (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRawMaterial(@PathVariable Long id) {
        rawMaterialService.deleteRawMaterial(id);
        // Returns 204 No Content
        return ResponseEntity.noContent().build();
    }

    // --- 6. Business Query: Search by Name Fragment (GET) ---
    // Example: /api/v1/raw-materials/search?query=steel
    @GetMapping("/search")
    public ResponseEntity<List<RawMaterialResponse>> searchRawMaterials(@RequestParam String query) {
        List<RawMaterialResponse> materials = rawMaterialService.searchRawMaterials(query);
        return ResponseEntity.ok(materials);
    }

    // --- 7. Business Query: Low Stock Alert (GET) ---
    // Example: /api/v1/raw-materials/low-stock?threshold=50
    @GetMapping("/low-stock")
    public ResponseEntity<List<RawMaterialResponse>> getMaterialsAtOrBelowStock(@RequestParam(name = "threshold") Integer stockThreshold) {
        List<RawMaterialResponse> materials = rawMaterialService.getMaterialsAtOrBelowStock(stockThreshold);
        return ResponseEntity.ok(materials);
    }

    // --- 8. Business Query: Materials by Supplier ID (GET) ---
    // Example: /api/v1/raw-materials/by-supplier/1
    @GetMapping("/by-supplier/{supplierId}")
    public ResponseEntity<List<RawMaterialResponse>> getMaterialsBySupplier(@PathVariable Long supplierId) {
        List<RawMaterialResponse> materials = rawMaterialService.getMaterialsBySupplier(supplierId);
        return ResponseEntity.ok(materials);
    }
}