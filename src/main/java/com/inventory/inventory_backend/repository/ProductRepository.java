package com.inventory.inventory_backend.repository;

import com.inventory.inventory_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Jpa repository methods (can be used directly)
    // save()
    // findOne()
    // findById()
    // findAll()
    // count()
    // delete()
    // deleteById()
}
