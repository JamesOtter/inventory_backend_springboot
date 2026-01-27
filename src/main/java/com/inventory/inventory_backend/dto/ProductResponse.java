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
    }
}
