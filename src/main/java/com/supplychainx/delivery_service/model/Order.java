package com.supplychainx.delivery_service.model;

import com.supplychainx.production_service.model.Product; // Import Product from the production module
import com.supplychainx.supply_service.model.enums.OrderStatus; // Import OrderStatus from the supply module
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "customer_orders") // Using 'customer_orders' to avoid conflict with SQL 'order' keyword
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true) // Adding callSuper for completeness, but the excludes are key
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude // <--- CRITICAL FIX: Prevents infinite loop with Customer
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

    // Additional fields commonly required for orders
    private LocalDate orderDate;

    @PrePersist
    public void prePersist() {
        if (this.orderDate == null) {
            this.orderDate = LocalDate.now();
        }
        if (this.status == null) {
            this.status = OrderStatus.IN_PREPARATION;
        }
    }
}