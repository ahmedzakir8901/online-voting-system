package com.voting.Entity;//change kiya hai entity sai Entity main

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// @Entity tells Spring: "This class maps to a database table."
// By default, the table name is the class name (lowercase). But your table is named 'users', so we specify it.
@Entity
@Table(name = "users")

// Lombok annotations – they generate boilerplate code at compile time:
// @Data = getters, setters, toString, equals, hashCode
// @NoArgsConstructor = constructor with no arguments (required by JPA)
// @AllArgsConstructor = constructor with all arguments (useful for building objects)
// @Builder = lets us create objects with a fluent pattern: User.builder().username("john").build()
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    // @Id marks the primary key.
    // @GeneratedValue tells JPA to auto-increment the ID (IDENTITY strategy matches your MySQL AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // @Column is optional if the field name matches the column name exactly.
    // But we specify it for clarity and to add constraints like unique, nullable, length.
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    // This will store the BCrypt-hashed password. Length 255 is safe for hashed values.
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDateTime dateOfBirth;  // You can use LocalDate if you don't need time.

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(name = "country", length = 50)
    private String country;  // default 'Pakistan' will be set in your code, not here.

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_picture", length = 255)
    private String profilePicture;

    // JPA uses 'isVerified' as the field name, but column is 'is_verified'.
    // We'll map explicitly.
    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "verification_token", length = 255)
    private String verificationToken;

    @Column(name = "token_expiry_date")
    private LocalDateTime tokenExpiryDate;

    @Column(name = "is_active")
    private boolean isActive = true;   // default value

    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "failed_attempts")
    private int failedAttempts = 0;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // --- Audit fields ---
    // @CreatedDate and @LastModifiedDate are automatically populated by Spring Data JPA.
    // But we need to enable auditing in our main application class with @EnableJpaAuditing.
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Relationships ---

    // A user can have many roles. This is a Many-to-Many relationship.
    // Fetch type EAGER means that whenever we load a User, JPA will also load all their roles.
    // This is convenient for security (we need roles to check permissions), but it can be a performance hit.
    // For now, it's acceptable.
    @ManyToMany(fetch = FetchType.EAGER)
    // @JoinTable defines the junction table (user_roles).
    // 'name' = the table name, 'joinColumns' = foreign key column for THIS entity (user_id),
    // 'inverseJoinColumns' = foreign key column for the OTHER entity (role_id).
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    // We use a Set because a user cannot have duplicate roles.
    // HashSet is a good default implementation.
    private Set<Role> roles = new HashSet<>();
}