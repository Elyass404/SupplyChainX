package com.supplychainx.production_service.repository;

import com.supplychainx.production_service.model.BillOfMaterial;
import com.supplychainx.production_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {

    //Consult the list of raw materials for a given product (BOM)
    List<BillOfMaterial> findByProduct(Product product);

    // Constraint: Ensure no duplicate (Product, RawMaterial) combination exists
    Optional<BillOfMaterial> findByProductIdAndRawMaterialId(Long productId, Long rawMaterialId);

    // Used by ProductionOrderService to get the recipe
    List<BillOfMaterial> findByProductId(Long productId);
}