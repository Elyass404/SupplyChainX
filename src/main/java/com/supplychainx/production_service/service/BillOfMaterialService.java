package com.supplychainx.production_service.service;

import com.supplychainx.production_service.dto.request.BillOfMaterialRequest;
import com.supplychainx.production_service.dto.response.BillOfMaterialResponse;

import java.util.List;

public interface BillOfMaterialService {

    // Add a new raw material to a product's recipe (BOM line item)
    BillOfMaterialResponse addMaterialToBOM(BillOfMaterialRequest request);

    // Modify the quantity of a raw material in a product's recipe
    BillOfMaterialResponse updateBOMItemQuantity(Long bomItemId, Double newQuantity);

    // Consult the list of raw materials for a given product (BOM)
    List<BillOfMaterialResponse> getBOMByProductId(Long productId);

    // Delete a raw material from a product's recipe
    void removeMaterialFromBOM(Long bomItemId);

    // Standard Get by ID
    BillOfMaterialResponse getBOMItemById(Long bomItemId);
}