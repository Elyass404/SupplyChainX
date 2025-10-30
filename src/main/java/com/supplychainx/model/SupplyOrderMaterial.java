package com.supplychainx.model;

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

    @ManyToOne
    @JoinColumn(name = "supply_order_id", nullable = false)
    private SupplyOrder supplyOrder;

    @ManyToOne
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(nullable = false)
    private Integer quantity;
}
