package com.supplychainx.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.SpringApplicationRunListener;

import java.util.List;

@Entity
@Table(name = "raw_material")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int stockMin;

    @Column(nullable = false)
    private String unit ;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "supplier_raw_material",
            joinColumns = @JoinColumn(name = "raw_material_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    @ToString.Exclude
    private List<Supplier> suppliers;

    private List<SupplyOrderMaterial>  supplyOrderMaterials;
}
