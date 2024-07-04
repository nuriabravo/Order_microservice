package com.ordersmicroservice.orders_microservice.services;

import com.ordersmicroservice.orders_microservice.models.OrderedProduct;
import com.ordersmicroservice.orders_microservice.repositories.OrderedProductRepository;
import com.ordersmicroservice.orders_microservice.services.impl.OrderedProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderedProductServiceTest {
    @Mock
    OrderedProductRepository orderedProductRepository;
    @InjectMocks
    OrderedProductServiceImpl orderedProductsService;
    Long orderId;
    private List<OrderedProduct> orderedProducts;

    @BeforeEach
    void setup(){
        orderId = 1L;
        OrderedProduct orderedProduct1 = OrderedProduct.builder()
                .productId(1L)
                .quantity(3)
                .build();
        OrderedProduct orderedProduct2 = OrderedProduct.builder()
                .productId(2L)
                .quantity(5)
                .build();
        orderedProducts = List.of(orderedProduct1, orderedProduct2);
    }

    @Test
    @DisplayName("Testing method retrieves all products from an order with given id")
    void testGetAllProductsFromOrder(){
        when(orderedProductRepository.findByOrderId(orderId)).thenReturn(orderedProducts);

        List<OrderedProduct> savedProducts = orderedProductsService.getAllProductsFromOrder(orderId);
        assertThat(savedProducts)
                .isNotNull()
                .isNotEqualTo(Collections.emptyList())
                .isEqualTo(orderedProducts);
    }
}
