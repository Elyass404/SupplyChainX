package com.supplychainx.production_service.controller;

import com.supplychainx.production_service.dto.request.BillOfMaterialRequest;
import com.supplychainx.production_service.dto.response.BillOfMaterialResponse;
import com.supplychainx.production_service.service.BillOfMaterialService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/production/boms")
@RequiredArgsConstructor
public class BillOfMaterialController {

    private final BillOfMaterialService billOfMaterialService;

    // Add a new raw material to a product's recipe
    @PostMapping
    public ResponseEntity<BillOfMaterialResponse> addMaterialToBOM(@Valid @RequestBody BillOfMaterialRequest request) {
        BillOfMaterialResponse response = billOfMaterialService.addMaterialToBOM(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        // Returns 201 Created
        return ResponseEntity.created(location).body(response);
    }

    // Modify the quantity of a raw material in a recipe
    @PutMapping("/{bomItemId}")
    public ResponseEntity<BillOfMaterialResponse> updateBOMItemQuantity(
            @PathVariable Long bomItemId,
            @RequestParam @Positive(message = "New quantity must be positive") Double newQuantity) {

        BillOfMaterialResponse response = billOfMaterialService.updateBOMItemQuantity(bomItemId, newQuantity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bomItemId}")
    public ResponseEntity<BillOfMaterialResponse> getBOMItemById(@PathVariable Long bomItemId) {
        return ResponseEntity.ok(billOfMaterialService.getBOMItemById(bomItemId));
    }

    // Consult the list of raw materials for a given product
    // The endpoint is designed to filter the list of BOM items by the parent Product ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<BillOfMaterialResponse>> getBOMByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(billOfMaterialService.getBOMByProductId(productId));
    }

    // Delete a raw material from a product's recipe
    @DeleteMapping("/{bomItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204
    public void removeMaterialFromBOM(@PathVariable Long bomItemId) {
        billOfMaterialService.removeMaterialFromBOM(bomItemId);
    }
}