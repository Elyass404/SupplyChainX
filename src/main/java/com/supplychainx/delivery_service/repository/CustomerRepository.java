package com.supplychainx.delivery_service.repository;

import com.supplychainx.delivery_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {


    List<Customer> findByNameContainingIgnoreCase(String name);

}