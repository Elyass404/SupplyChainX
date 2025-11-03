package com.supplychainx.repository;

import com.supplychainx.model.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RawMaterialRepository  extends JpaRepository<RawMaterial,Long> {

    // Find a material by exact name (useful for uniqueness checks)
    Optional<RawMaterial> findByName(String name);

    // Check if a material with the given name already exists
    boolean existsByName(String name);

    // Search materials by name fragment (for UI search)
    List<RawMaterial> findByNameContainingIgnoreCase(String fragment);

    // Find materials that are at or below a given critical stock threshold
    List<RawMaterial> findByStockLessThanEqual(Integer stockThreshold);

    // Find materials supplied by a specific supplier (uses join table)
    List<RawMaterial> findBySuppliers_Id(Long supplierId);

}
