package com.khoipd8.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street", length = 255) // Renamed from 'address' in User to 'street' for clarity
    private String street;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    // Optional: If an address can belong to multiple users (e.g., shared shipping address),
    // or if you want to explicitly map back. For a direct 1-to-1, this isn't strictly needed here,
    // but useful for bi-directional mapping.
    // If a User *must* have an Address, and an Address belongs to *one* User, this would be appropriate.
    // @OneToOne(mappedBy = "address")
    // private User user;
}
