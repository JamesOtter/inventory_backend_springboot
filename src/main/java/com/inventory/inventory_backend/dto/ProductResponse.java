package com.inventory.inventory_backend.dto;

import com.inventory.inventory_backend.model.Product;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private int quantity;
    private BigDecimal price;
    private String imageUrl;

    private Long userId;
    private String username;

    public ProductResponse(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.quantity = product.getQuantity();
        this.price = product.getPrice();
        this.userId = product.getUser().getId();
        this.username = product.getUser().getUsername();

        if(product.getImageName() != null && !product.getImageName().isEmpty()){
            this.imageUrl = "http://localhost:8080/uploads/products/" + product.getImageName();
        } else {
            this.imageUrl = "http://localhost:8080/uploads/products/default.png";
        }
    }
}
