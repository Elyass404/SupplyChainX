package com.supplychainx.repository;

import com.supplychainx.model.SupplyOrderMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyOrderMaterialRepository extends JpaRepository<SupplyOrderMaterial,Long> {

    // Find all material items for a specific supply order
    List<SupplyOrderMaterial> findBySupplyOrder_id(Long supplyOrderId);

    // Find all supply orders that include a specific raw material
    List<SupplyOrderMaterial> findByRawMaterial_Id(Long rawMaterialId);

    // Check if a specific material already exists in a given supply order
    boolean existsBySupplyOrder_IdAndRawMaterial_Id(Long supplyOrderId, Long rawMaterialId);
}
