package com.supplychainx.service;

import com.supplychainx.model.RawMaterial;

import java.util.List;
import java.util.Optional;

public interface RawMaterialService {
    RawMaterial createRawMaterial(RawMaterial rawMaterial);

    RawMaterial updateRawMaterial(Long id ,RawMaterial rawMaterial);

    void deleteRawMaterial(RawMaterial rawMaterial);

    Optional<RawMaterial> findRawMaterialById(Long id);

    List<RawMaterial> findAllRawMaterial();


    RawMaterial findByName(String name);

    boolean existsByName(String name);

    List<RawMaterial> findByNameContainingIgnoreCase(String fragment);

    List<RawMaterial> findByStockLessThanEqual(Integer stockThreshold);

    List<RawMaterial> findBySuppliers_Id(Long supplierId);
}
