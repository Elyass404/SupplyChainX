package com.supplychainx.service;

import com.supplychainx.model.RawMaterial;
import java.util.List;
import java.util.Optional;

public interface RawMaterialService {

    // CRUD Operations
    RawMaterial createRawMaterial(RawMaterial rawMaterial);

    RawMaterial updateRawMaterial(Long id ,RawMaterial rawMaterial);

    void deleteRawMaterial(Long id );

    // Retrieval
    Optional<RawMaterial> findRawMaterialById(Long id);

    List<RawMaterial> findAllRawMaterial();

    Optional<RawMaterial> findByName(String name); // Updated return type

    // Business & Utility Methods
    boolean existsByName(String name);

    List<RawMaterial> searchRawMaterials(String name);

    // Business Logic Methods
    List<RawMaterial> getMaterialsAtOrBelowStock(Integer stockThreshold);

    List<RawMaterial> getMaterialsBySupplier(Long supplierId);

}