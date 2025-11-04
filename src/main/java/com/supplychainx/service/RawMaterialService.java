package com.supplychainx.service;

import com.supplychainx.dto.request.RawMaterialRequest;
import com.supplychainx.dto.response.RawMaterialResponse;

import java.util.List;
import java.util.Optional;

public interface RawMaterialService {

    // --- CRUD Operations ---
    RawMaterialResponse createRawMaterial(RawMaterialRequest request);

    RawMaterialResponse updateRawMaterial(Long id ,RawMaterialRequest request);

    void deleteRawMaterial(Long id );

    // --- Retrieval ---
    RawMaterialResponse findRawMaterialById(Long id); // Updated to return DTO directly

    List<RawMaterialResponse> findAllRawMaterial();

    Optional<RawMaterialResponse> findByName(String name);

    // --- Business & Utility Methods ---
    boolean existsByName(String name);

    List<RawMaterialResponse> searchRawMaterials(String query);

    List<RawMaterialResponse> getMaterialsAtOrBelowStock(Integer stockThreshold);

    List<RawMaterialResponse> getMaterialsBySupplier(Long supplierId);
}