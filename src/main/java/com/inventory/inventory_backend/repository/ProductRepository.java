package com.inventory.inventory_backend.repository;

import com.inventory.inventory_backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByUserId(Long userId, Pageable pageable);

    Page<Product> findByUserIdAndNameContainingIgnoreCase(Long userId, String keyword, Pageable pageable);

    // Jpa repository methods (can be used directly)
    // save()
    // findOne()
    // findById()
    // findAll()
    // count()
    // delete()
    // deleteById()
}
