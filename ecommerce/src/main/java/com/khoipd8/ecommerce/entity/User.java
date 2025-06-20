package com.khoipd8.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.khoipd8.ecommerce.utils.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // --- REMOVED ADDRESS FIELDS HERE ---
    // @Column(name = "address", length = 255)
    // private String address;
    // @Column(name = "city", length = 50)
    // private String city;
    // @Column(name = "state", length = 50)
    // private String state;
    // @Column(name = "country", length = 50)
    // private String country;
    // @Column(name = "postal_code", length = 20)
    // private String postalCode;
    // --- END REMOVED ADDRESS FIELDS ---

    // NEW: One-to-One relationship with Address entity
    // This assumes a User has one primary address.
    // @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "address_id", referencedColumnName = "id")
    // private Address address;

    // If a user can have MULTIPLE addresses (e.g., billing, shipping),
    // you would use @OneToMany:
    // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id") // This creates a foreign key 'user_id' in the 'addresses' table
    // private Set<Address> addresses = new HashSet<>();

    // For simplicity, I'll go with a single primary address using @OneToOne.
    // If you need multiple addresses, change this to @OneToMany and use a Collection.
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true) // Cascade.ALL will save/update/delete Address when User is saved/updated/deleted
    @JoinColumn(name = "address_id", referencedColumnName = "id") // Foreign key 'address_id' in 'users' table, referencing 'id' in 'addresses' table
    @JsonIgnore
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "phone_verified", nullable = false)
    private Boolean phoneVerified = false;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "login_attempts", nullable = false)
    private Integer loginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "email_verification_token_expiry")
    private LocalDateTime emailVerificationTokenExpiry;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // Constructors
    public User() {}

    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = UserStatus.INACTIVE;
        this.emailVerified = false;
        this.phoneVerified = false;
        this.loginAttempts = 0;
        // Address is not set here, it would be set separately or via a DTO
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
    }

    public boolean isPasswordResetTokenValid() {
        return passwordResetToken != null &&
                passwordResetTokenExpiry != null &&
                passwordResetTokenExpiry.isAfter(LocalDateTime.now());
    }

    public boolean isEmailVerificationTokenValid() {
        return emailVerificationToken != null &&
                emailVerificationTokenExpiry != null &&
                emailVerificationTokenExpiry.isAfter(LocalDateTime.now());
    }
}