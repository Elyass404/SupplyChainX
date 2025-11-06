package com.supplychainx.production_service.service.impl;

import com.supplychainx.dto.response.RawMaterialSimpleResponse;
import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.supply_service.model.RawMaterial;
import com.supplychainx.production_service.dto.request.BillOfMaterialRequest;
import com.supplychainx.production_service.dto.response.BillOfMaterialResponse;
import com.supplychainx.production_service.mapper.BillOfMaterialMapper;
import com.supplychainx.production_service.model.BillOfMaterial;
import com.supplychainx.production_service.model.Product;
import com.supplychainx.production_service.repository.BillOfMaterialRepository;
import com.supplychainx.production_service.repository.ProductRepository;
import com.supplychainx.production_service.service.BillOfMaterialService;
import com.supplychainx.supply_service.repository.RawMaterialRepository; // Dependency on external module
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BillOfMaterialServiceImpl implements BillOfMaterialService {

    private final BillOfMaterialRepository billOfMaterialRepository;
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository; // Dependency for external entity
    private final BillOfMaterialMapper billOfMaterialMapper;

    @Override
    public BillOfMaterialResponse addMaterialToBOM(BillOfMaterialRequest request) {
        // 1. Validate Product existence
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with ID: " + request.getProductId()));

        // 2. Validate Raw Material existence
        RawMaterial rawMaterial = rawMaterialRepository.findById(request.getRawMaterialId())
                .orElseThrow(() -> new RessourceNotFoundException("Raw Material not found with ID: " + request.getRawMaterialId()));

        // 3. Enforce Uniqueness Constraint: Check if this material is already in the BOM for this product
        if (billOfMaterialRepository.findByProductIdAndRawMaterialId(request.getProductId(), request.getRawMaterialId()).isPresent()) {
            throw new IllegalArgumentException("Raw Material '" + rawMaterial.getName() + "' is already defined in the BOM for product '" + product.getName() + "'.");
        }

        // 4. Create and Save the BOM Line Item (US23)
        BillOfMaterial bom = BillOfMaterial.builder()
                .product(product)
                .rawMaterial(rawMaterial)
                .requiredQuantity(request.getRequiredQuantity())
                .build();

        BillOfMaterial savedBom = billOfMaterialRepository.save(bom);
        return billOfMaterialMapper.toResponse(savedBom);
    }

    @Override
    public BillOfMaterialResponse updateBOMItemQuantity(Long bomItemId, Double newQuantity) {
        // Modify the required quantity
        BillOfMaterial existingBom = billOfMaterialRepository.findById(bomItemId)
                .orElseThrow(() -> new RessourceNotFoundException("BOM line item not found with ID: " + bomItemId));

        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive. Use the delete operation to remove the item.");
        }

        existingBom.setRequiredQuantity(newQuantity);
        BillOfMaterial updatedBom = billOfMaterialRepository.save(existingBom);
        return billOfMaterialMapper.toResponse(updatedBom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterialResponse> getBOMByProductId(Long productId) {
        // Consult BOM by Product ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with ID: " + productId));

        List<BillOfMaterial> boms = billOfMaterialRepository.findByProduct(product);
        return billOfMaterialMapper.toResponseList(boms);
    }

    @Override
    @Transactional(readOnly = true)
    public BillOfMaterialResponse getBOMItemById(Long bomItemId) {
        BillOfMaterial bom = billOfMaterialRepository.findById(bomItemId)
                .orElseThrow(() -> new RessourceNotFoundException("BOM line item not found with ID: " + bomItemId));
        return billOfMaterialMapper.toResponse(bom);
    }

    @Override
    public void removeMaterialFromBOM(Long bomItemId) {
        // Delete a raw material from a product's recipe
        if (!billOfMaterialRepository.existsById(bomItemId)) {
            throw new RessourceNotFoundException("BOM line item not found with ID: " + bomItemId);
        }
        // Note: No production-specific deletion rule applies here,
        // as the ProductionOrder only holds a reference to the Product, not the BOM lines.

        billOfMaterialRepository.deleteById(bomItemId);
    }
}