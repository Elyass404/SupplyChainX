package com.supplychainx.repository;

import com.supplychainx.model.SupplyOrder;
import com.supplychainx.model.enums.SupplyOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Long> {

    // Find all orders placed by a specific supplier
    List<SupplyOrder> findBySupplier_Id(Long supplierId);

    // Find all orders with a specific status (e.g., PENDING, RECEIVED)
    List<SupplyOrder> findByStatus(SupplyOrderStatus status);

    // Find orders placed within a specific date range
    List<SupplyOrder> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    // Check if a supplier has an existing order with a given date (example for duplicate prevention)
    boolean existsBySupplier_IdAndOrderDate(Long supplierId, LocalDate orderDate);

    List<SupplyOrder> findByStatusOrderByOrderDateDesc(SupplyOrderStatus status);
}
