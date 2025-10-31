package com.supplychainx.repository;

import com.supplychainx.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByEmail(String email); // to check if a supplier exist by email, custom query method
}
