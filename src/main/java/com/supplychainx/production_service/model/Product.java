package com.supplychainx.production_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "production_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corresponds to idProduct

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer productionTime; // Temps de fabrication (heures)

    @Column(nullable = false)
    private Double cost; // Coût unitaire

    @Column(nullable = false)
    private Integer stock; // Quantité en stock
}