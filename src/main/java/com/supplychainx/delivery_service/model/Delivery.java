package com.supplychainx.delivery_service.model;

import com.supplychainx.supply_service.model.enums.DeliveryStatus; // Import DeliveryStatus
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicle;

    @Column(nullable = false)
    private String driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private String deliveryAddress; // The correct location for the address field

    @Column(nullable = false)
    private LocalDate deliveryDate;

    @Column(nullable = false)
    private Double cost; // Cost total (US40)

    // One-to-One relationship with Order
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}