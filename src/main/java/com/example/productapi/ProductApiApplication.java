package com.example.productapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductApiApplication {

    private static final Logger log = LoggerFactory.getLogger(ProductApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ProductApiApplication.class, args);
        log.info("Product API started successfully");
    }
}
