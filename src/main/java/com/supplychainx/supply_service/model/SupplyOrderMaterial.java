package com.supplychainx.supply_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supply_order_material")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplyOrderMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relationships ---

    // ManyToOne: Each line item belongs to one SupplyOrder
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_order_id", nullable = false)
    @ToString.Exclude
    private SupplyOrder supplyOrder;

    // ManyToOne: Each line item specifies one RawMaterial
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    @ToString.Exclude
    private RawMaterial rawMaterial;

    // --- Attributes ---

    @Column(nullable = false)
    private Integer quantity;
}