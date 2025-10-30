package com.supplychainx.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name ="supplier")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column // so the nullable is true by default
    private Double rating;

    @Column
    private int leadTime;

    @OneToMany(mappedBy = "supplier" , cascade = CascadeType.ALL)
    private List<SupplyOrder> supplyOrders;
}
