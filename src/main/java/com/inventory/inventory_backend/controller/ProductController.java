package com.inventory.inventory_backend.controller;

import com.inventory.inventory_backend.dto.ProductRequest;
import com.inventory.inventory_backend.dto.ProductUpdateRequest;
import com.inventory.inventory_backend.exception.FieldValidationException;
import com.inventory.inventory_backend.model.Product;
import com.inventory.inventory_backend.repository.ProductRepository;
import com.inventory.inventory_backend.repository.UserRepository;
import com.inventory.inventory_backend.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")     // declare that all url in controller start with /api
@CrossOrigin("http://localhost:5173")
public class ProductController {

    @Autowired      // to inject repository bean to local variable
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/products")
    public List<Product> getProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam(required = false) String keyword){
        Long userId = userDetails.getId();

        if(keyword == null || keyword.isBlank()){
            return productRepository.findByUserId(userId);
        }

        return productRepository.findByUserIdAndNameContainingIgnoreCase(userId, keyword);
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .user(userRepository.getReferenceById(userDetails.getId()))
                .build();

        productRepository.save(product);

        return ResponseEntity.ok("Product Created Successfully");
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id, @Valid @RequestBody ProductUpdateRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new FieldValidationException("general", "Product not found"));

        if(!product.getUser().getId().equals(userDetails.getId())){
            throw new FieldValidationException("general", "You are not allowed to update this product");
        }

        if(request.getName() != null){
            product.setName(request.getName());
        }
        if(request.getDescription() != null){
            product.setDescription(request.getDescription());
        }
        if(request.getQuantity() != null){
            product.setQuantity(request.getQuantity());
        }
        if(request.getPrice() != null){
            product.setPrice(request.getPrice());
        }

        productRepository.save(product);

        return ResponseEntity.ok("Product Updated Successfully");
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new FieldValidationException("general", "Product not found"));

        if(!product.getUser().getId().equals(userDetails.getId())){
            throw new FieldValidationException("general", "You are not allowed to delete this product");
        }

        productRepository.deleteById(id);

        return ResponseEntity.ok("Product Deleted Successfully");
    }
}
