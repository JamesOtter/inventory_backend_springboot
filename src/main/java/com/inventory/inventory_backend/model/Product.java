package com.inventory.inventory_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder                        // Used to create data into database
@Entity                         // Indicate that the class is a persistent Java class
@Table(name = "products")
public class Product {

    @Id     // annotations for primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PK, auto-increment

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)                  // Many products belong to one user (Lazy avoid loading the User every time)
    @JoinColumn(name = "user_id", nullable = false)     // foreign key column
    private User user;                                  // Link to User


    // All code below was comment because of using Lombok, thus no need to do it all by myself
//    // Constructor
//    public Product() {}     // JPA requires a no-args constructor
//
//    public Product(String name, String description, int quantity, BigDecimal price){
//        this.name = name;
//        this.description = description;
//        this.quantity = quantity;
//        this.price = price;
//    }
//
//    // Getter and Setters
//    public Long getId() { return id; }
//    public void setId(long id) { this.id = id; }
//
//    public String getName() { return name; }
//    public void setName() { this.name = name; }
//
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }
//
//    public int getQuantity() { return quantity; }
//    public void setQuantity(int quantity) { this.quantity = quantity; }
//
//    public BigDecimal getPrice() { return price; }
//    public void setPrice(BigDecimal price) { this.price = price; }

}
