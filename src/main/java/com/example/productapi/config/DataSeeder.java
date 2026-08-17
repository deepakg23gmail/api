package com.example.productapi.config;

import com.example.productapi.entity.Product;
import com.example.productapi.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<Product> products = List.of(
                        new Product("Laptop Pro 16",
                                "High-performance laptop with 16-inch Retina display, 32GB RAM, and 1TB SSD",
                                new BigDecimal("1999.99"), 25, "Electronics"),
                        new Product("Wireless Mouse",
                                "Ergonomic wireless mouse with silent clicks and long battery life",
                                new BigDecimal("49.99"), 150, "Accessories"),
                        new Product("USB-C Hub",
                                "7-in-1 USB-C hub with HDMI, USB 3.0, SD card reader, and power delivery",
                                new BigDecimal("79.99"), 80, "Accessories"),
                        new Product("Mechanical Keyboard",
                                "RGB mechanical keyboard with Cherry MX switches and aluminum frame",
                                new BigDecimal("149.99"), 60, "Accessories"),
                        new Product("4K Monitor 27\"",
                                "27-inch 4K UHD monitor with HDR support and adjustable stand",
                                new BigDecimal("599.99"), 35, "Electronics")
                );
                repository.saveAll(products);
                log.info("Sample data loaded: {} products", products.size());
            }
        };
    }
}
