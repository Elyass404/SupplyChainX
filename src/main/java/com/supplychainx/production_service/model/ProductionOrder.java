package com.supplychainx.production_service.model;

import com.supplychainx.supply_service.model.enums.ProductionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "production_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corresponds to idOrder

    // The finished product to be produced
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // The quantity requested (quantity commandée)
    @Column(nullable = false)
    private Integer quantity;

    // Status of the order
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductionStatus status;

    // Date de début
    private LocalDate startDate;

    // Date de fin estimée
    private LocalDate endDate;

    // Default the status upon creation
    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = ProductionStatus.PENDING;
        }
    }
}