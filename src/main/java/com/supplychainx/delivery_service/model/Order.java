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
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private String address; // Specific delivery address for this order (as discussed)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Additional fields commonly required for orders (based on US35-US39 context)
    private LocalDate orderDate;

    // Use @PrePersist to set default values before saving
    @PrePersist
    public void prePersist() {
        if (this.orderDate == null) {
            this.orderDate = LocalDate.now();
        }
        if (this.status == null) {
            // Assuming PENDING is the initial status for a new customer order
            this.status = OrderStatus.IN_PREPARATION;
        }
    }
}