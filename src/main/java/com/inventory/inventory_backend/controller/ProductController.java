package com.inventory.inventory_backend.controller;

import com.inventory.inventory_backend.model.Product;
import com.inventory.inventory_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")     // declare that all url in controller start with /api
@CrossOrigin("http://localhost:5173/")
public class ProductController {

    @Autowired      // to inject repository bean to local variable
    private ProductRepository productRepository;

    @GetMapping("/products")
    public List<Product> fetchProduct(){
        return productRepository.findAll();
    }
}
