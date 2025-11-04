package com.supplychainx.repository;

import com.supplychainx.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // 1. Find a supplier by exact name (used for retrieval and uniqueness checks)
    Optional<Supplier> findByName(String name);

    // 2. Check if a supplier exists by exact name
    boolean existsByName(String name);

    // 3. Check if a supplier exists by email (already provided, but crucial)
    boolean existsByEmail(String email);

    // 4. Search suppliers by name fragment (for UI search functionality)
    List<Supplier> findByNameContainingIgnoreCase(String fragment);

    // 5. Find suppliers with a rating above a certain threshold
    List<Supplier> findByRatingGreaterThanEqual(Double minRating);
}