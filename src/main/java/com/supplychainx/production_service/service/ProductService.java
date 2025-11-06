package com.supplychainx.production_service.service;

import com.supplychainx.production_service.dto.request.ProductRequest;
import com.supplychainx.production_service.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    // US22: Search a product by name
    List<ProductResponse> searchProductsByName(String name);
}