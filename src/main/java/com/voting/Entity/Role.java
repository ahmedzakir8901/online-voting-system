package com.voting.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String roleName;  // e.g., "ROLE_ADMIN", "ROLE_VOTER"

    @Column(name = "description", length = 255)
    private String description;

    // Note: We do NOT add a 'users' field here. That would create a bidirectional relationship.
    // We prefer unidirectional (User -> Role) to keep things simpler and avoid lazy-loading issues.
    // The join table is managed from the User side.
}