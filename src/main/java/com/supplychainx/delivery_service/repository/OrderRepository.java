package com.supplychainx.delivery_service.repository;

import com.supplychainx.delivery_service.model.Order;
import com.supplychainx.supply_service.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

}