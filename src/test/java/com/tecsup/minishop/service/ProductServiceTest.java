package com.tecsup.minishop.service;

import com.tecsup.minishop.model.Product;
import com.tecsup.minishop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    @DisplayName("Debe guardar un producto válido exitosamente")
    void shouldSaveProductSuccessfully() {
        Product product = Product.builder()
                .name("Teclado Mecánico")
                .price(150.0)
                .stock(10)
                .build();
        Product savedProduct = Product.builder()
                .id(1L)
                .name("Teclado Mecánico")
                .price(150.0)
                .stock(10)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.save(product);

        assertNotNull(result.getId());
        assertEquals("Teclado Mecánico", result.getName());
        assertEquals(150.0, result.getPrice());
        assertEquals(10, result.getStock());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el precio es cero o negativo")
    void shouldThrowExceptionWhenPriceIsZeroOrNegative() {
        Product product = Product.builder()
                .name("Producto Inválido")
                .price(0.0)
                .stock(5)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.save(product);
        });

        assertEquals("El precio debe ser mayor a cero", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el stock es negativo")
    void shouldThrowExceptionWhenStockIsNegative() {
        Product product = Product.builder()
                .name("Producto Inválido")
                .price(10.0)
                .stock(-5)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.save(product);
        });

        assertEquals("El stock no puede ser negativo", exception.getMessage());
        verify(productRepository, never()).save(any());
    }
}
