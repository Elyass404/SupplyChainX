package com.supplychainx.production_service.model;

import com.supplychainx.supply_service.model.RawMaterial;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bill_of_materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillOfMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many BOM items belong to one Finished Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // One BOM item requires one Raw Material
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    // Quantity of RawMaterial needed for ONE FinishedProduct (US23)
    @Column(nullable = false)
    private Double requiredQuantity;
}