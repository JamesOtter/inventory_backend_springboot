package com.inventory.inventory_backend.repository;

import com.inventory.inventory_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUserId(Long userId);

    List<Product> findByUserIdAndNameContainingIgnoreCase(Long userId, String keyword);

    // Jpa repository methods (can be used directly)
    // save()
    // findOne()
    // findById()
    // findAll()
    // count()
    // delete()
    // deleteById()
}
