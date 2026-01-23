package com.inventory.inventory_backend;

import com.inventory.inventory_backend.model.Product;
import com.inventory.inventory_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class InventoryBackendApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(InventoryBackendApplication.class, args);
	}

	@Autowired
	private ProductRepository productRepository;

	@Override
	public void run(String... args) throws Exception {
		Product product1 = Product.builder()
				.name("Laptop")
				.description("14 inch business laptop")
				.price(new BigDecimal("3500.00"))
				.quantity(10)
				.build();

		Product product2 = Product.builder()
				.name("Mouse")
				.description("Wireless mouse")
				.price(new BigDecimal("30.00"))
				.quantity(50)
				.build();

		Product product3 = Product.builder()
				.name("Keyboard")
				.description("Mechanical keyboard")
				.price(new BigDecimal("200.00"))
				.quantity(20)
				.build();

//		productRepository.save(product1);
//		productRepository.save(product2);
//		productRepository.save(product3);
	}
}
