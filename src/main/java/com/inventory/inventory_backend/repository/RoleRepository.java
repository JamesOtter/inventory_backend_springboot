package com.inventory.inventory_backend.repository;

import com.inventory.inventory_backend.model.ERole;
import com.inventory.inventory_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
