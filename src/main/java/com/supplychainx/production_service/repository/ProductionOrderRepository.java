package com.supplychainx.production_service.repository;

import com.supplychainx.production_service.model.Product;
import com.supplychainx.production_service.model.ProductionOrder;
import com.supplychainx.supply_service.model.enums.ProductionStatus; // 🔑 REQUIRED IMPORT

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    /*
     Consult the status of orders by filtering on status.
     */
    List<ProductionOrder> findByStatus(ProductionStatus status);

    /**
     * Constraint: Checks if any ProductionOrder exists associated with the given Product.
     */
    boolean existsByProduct(Product product);
}