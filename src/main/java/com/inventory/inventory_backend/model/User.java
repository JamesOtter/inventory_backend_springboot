package com.inventory.inventory_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)                        // Many-to-many relationship between Users and Role
    @JoinTable(name = "user_roles",                             // Name of joined table
        joinColumns = @JoinColumn(name = "user_id"),            // Refer to User
        inverseJoinColumns = @JoinColumn(name = "role_id"))     // Refer to Role

    private Set<Role> roles = new HashSet<>();                  // Use Set avoid duplicate roles for same user
}
