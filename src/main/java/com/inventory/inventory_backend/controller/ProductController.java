package com.inventory.inventory_backend.controller;

import com.inventory.inventory_backend.dto.ProductRequest;
import com.inventory.inventory_backend.dto.ProductResponse;
import com.inventory.inventory_backend.dto.ProductUpdateRequest;
import com.inventory.inventory_backend.exception.FieldValidationException;
import com.inventory.inventory_backend.model.Product;
import com.inventory.inventory_backend.repository.ProductRepository;
import com.inventory.inventory_backend.repository.UserRepository;
import com.inventory.inventory_backend.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")     // declare that all url in controller start with /api
@CrossOrigin("http://localhost:5173")
public class ProductController {

    @Autowired      // to inject repository bean to local variable
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // For logging purpose
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/products")
    public List<ProductResponse> getProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam(required = false) String keyword){
        Long userId = userDetails.getId();

        List<Product> products;

        if(keyword == null || keyword.isBlank()){
            products = productRepository.findByUserId(userId);
        }else{
            products = productRepository.findByUserIdAndNameContainingIgnoreCase(userId, keyword);
        }

        // stream() - process each product in products, one by one
        // map() - transform each element to ProductResponse
        return products.stream().map(ProductResponse::new).toList();
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){

        // Logging (API Entry)
        log.info("User {} is creating a product", userDetails.getId());

        MultipartFile image = request.getImage();
        validateImage(image);
        String imageName = storeProductImage(image);

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .imageName(imageName)
                .user(userRepository.getReferenceById(userDetails.getId()))
                .build();

        try{
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Failed to save product {} by user {}", product.getName(), userDetails.getId(), e);
            throw new FieldValidationException("general", "Failed to create product");
        };

        log.info("Product created successfully with ID={} by user {}", product.getId(), userDetails.getId());

        return ResponseEntity.ok("Product Created Successfully");
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("id") Long id, @Valid @ModelAttribute ProductUpdateRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){

        log.info("User {} updating product {}", userDetails.getId(), id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product {} not found during update", id);
                    return new FieldValidationException("general", "Product not found");
                });

        if(!product.getUser().getId().equals(userDetails.getId())){
            log.warn("User {} try to update product {} which is not allowed",userDetails.getId(), id);
            throw new FieldValidationException("general", "You are not allowed to update this product");
        }

        MultipartFile image = request.getImage();

        if (image != null && !image.isEmpty()) {
            deleteOldImage(product.getImageName());

            validateImage(image);
            String imageName = storeProductImage(image);

            product.setImageName(imageName);
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

        try{
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Failed to update product {} by user {}", id, userDetails.getId(), e);
            throw new FieldValidationException("general", "Failed to update product");
        };

        log.info("Product {} updated successfully", id);

        return ResponseEntity.ok(new ProductResponse(product));
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){

        log.info("User {} deleting product {}", userDetails.getId(), id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product {} not found during delete", id);
                    return new FieldValidationException("general", "Product not found");
                });

        if(!product.getUser().getId().equals(userDetails.getId())){
            log.warn("User {} try to delete product {} which is not allowed",userDetails.getId(), id);
            throw new FieldValidationException("general", "You are not allowed to delete this product");
        }

        try{
            productRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete product {} by user {}", id, userDetails.getId(), e);
            throw new FieldValidationException("general", "Failed to delete product");
        };

        log.info("Product {} deleted successfully", id);

        return ResponseEntity.ok("Product Deleted Successfully");
    }

    private void validateImage(MultipartFile image){
        if(image == null || image.isEmpty()){
            return;
        }

        // Max size: 5MB
        long maxSize = 5 * 1024 * 1024;
        if(image.getSize() > maxSize){
            throw new FieldValidationException("image", "Image size must be <= 5MB");
        }

        // Check Content type
        List<String> allowedTypes = List.of(
                "image/jpeg",
                "image/png",
                "image/webp",
                "image/jpg"
        );
        if(!allowedTypes.contains(image.getContentType())){
            throw new FieldValidationException("image", "Only JPG, PNG, JPEG, WEBP images are allowed");
        }
    }

    private String storeProductImage(MultipartFile image){
        if (image == null || image.isEmpty()) {
            return "default.png"; // image is optional
        }

        try {
            String uploadDir = "uploads/products/";
            Files.createDirectories(Paths.get(uploadDir));

            String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path imagePath = Paths.get(uploadDir, imageName);

            Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

            return imageName;

        } catch (IOException e) {
            throw new FieldValidationException("image", "Failed to store product image");
        }
    }

    private void deleteOldImage(String image){
        if(image.equals("default.png")){
            return;
        }

        try {
            Path oldImagePath = Paths.get("uploads/products/" + image);
            Files.deleteIfExists(oldImagePath);
        } catch (IOException e) {
            throw new FieldValidationException("image", "Failed to store product image");
        }
    }
}
