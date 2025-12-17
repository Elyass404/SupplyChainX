package com.supplychainx.supply_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //this is the actual token that will be be generated and get stocked in this colum
    @Column(nullable = false, unique = true)
    private String token;

    //after i consulted the internet i found that "Instant" type more convinient than "Date"
    //is the modern (Java 8+) standard. It represents a specific moment on the timeline (UTC)
    // which is perfect for security expirations because it doesn't get confused by Time Zones.
    @Column(nullable = false)
    private Instant expiryDate;

    //this will store the answer to the question of: has this token been cancelled
    private boolean revoked;

    //here we are relating the to token to its user, so one token can be related only to one user and one user can have only one token
    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;


}
