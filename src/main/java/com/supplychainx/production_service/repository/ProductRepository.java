package com.supplychainx.production_service.repository;

import com.supplychainx.production_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutRepository extends JpaRepository<Product,Long> {
}
