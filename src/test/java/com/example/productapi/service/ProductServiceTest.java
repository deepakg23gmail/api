package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.repository.ProductRepository;
import com.example.productapi.service.impl.ProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product = new Product("Laptop", "High-performance laptop", new BigDecimal("999.99"), 10, "Electronics");
        product.setId(1L);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productRequest = new ProductRequest("Laptop", "High-performance laptop",
                new BigDecimal("999.99"), 10, "Electronics");
    }

    @Test
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals(new BigDecimal("999.99"), response.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_Found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(99L));

        assertTrue(exception.getMessage().contains("Product"));
        assertTrue(exception.getMessage().contains("99"));
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void getAllProducts_Success() {
        Product product2 = new Product("Mouse", "Wireless mouse", new BigDecimal("29.99"), 50, "Accessories");
        product2.setId(2L);
        when(productRepository.findAll()).thenReturn(List.of(product, product2));

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(2, responses.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void updateProduct_Success() {
        ProductRequest updateRequest = new ProductRequest("Updated Laptop", "Updated description",
                new BigDecimal("1299.99"), 15, "Electronics");
        Product updatedProduct = new Product("Updated Laptop", "Updated description",
                new BigDecimal("1299.99"), 15, "Electronics");
        updatedProduct.setId(1L);
        updatedProduct.setCreatedAt(product.getCreatedAt());
        updatedProduct.setUpdatedAt(LocalDateTime.now());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponse response = productService.updateProduct(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Laptop", response.getName());
        assertEquals(new BigDecimal("1299.99"), response.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(99L, productRequest));
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProduct_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProduct(99L));
        verify(productRepository, never()).delete(any());
    }

    @Test
    void searchProducts_Success() {
        when(productRepository.searchProducts("Lap")).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.searchProducts("Lap");

        assertEquals(1, responses.size());
        assertEquals("Laptop", responses.getFirst().getName());
        verify(productRepository, times(1)).searchProducts("Lap");
    }

    @Test
    void getProductsByCategory_Success() {
        when(productRepository.findByCategory("Electronics")).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.getProductsByCategory("Electronics");

        assertEquals(1, responses.size());
        verify(productRepository, times(1)).findByCategory("Electronics");
    }
}
