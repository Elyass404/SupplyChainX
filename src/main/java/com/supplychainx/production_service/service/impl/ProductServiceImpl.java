package com.supplychainx.production_service.service.impl;

import com.supplychainx.production_service.dto.request.ProductRequest;
import com.supplychainx.production_service.dto.response.ProductResponse;
import com.supplychainx.exception.RessourceNotFoundException; // Assumed to be in com.supplychainx.exception
import com.supplychainx.production_service.mapper.ProductMapper;
import com.supplychainx.production_service.model.Product;
import com.supplychainx.production_service.repository.ProductRepository;
import com.supplychainx.production_service.repository.ProductionOrderRepository;
import com.supplychainx.production_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    // Used to enforce the deletion constraint (US20)
    private final ProductionOrderRepository productionOrderRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        // US18: Check for unique product name (standard)
        if (productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Product with name '" + request.getName() + "' already exists.");
        }

        Product newProduct = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(newProduct);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with ID: " + id));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productMapper.toResponseList(productRepository.findAll());
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with ID: " + id));

        // US19: Check for name uniqueness only if the name has changed
        if (!existingProduct.getName().equalsIgnoreCase(request.getName()) &&
                productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Another Product with name '" + request.getName() + "' already exists.");
        }

        // Update fields
        existingProduct.setName(request.getName());
        existingProduct.setProductionTime(request.getProductionTime());
        existingProduct.setCost(request.getCost());
        existingProduct.setStock(request.getStock()); // This will be managed by ProductionOrders later

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with ID: " + id));

        // BUSINESS RULE: Delete only if no associated production order exists (US20/Rule 76)
        if (productionOrderRepository.existsByProduct(product)) {
            throw new IllegalStateException("Cannot delete product: Associated Production Orders exist. (Rule 76)");
        }

        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        // US22: Search product by name
        return productMapper.toResponseList(productRepository.findByNameContainingIgnoreCase(name));
    }
}