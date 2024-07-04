package com.ordersmicroservice.orders_microservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordersmicroservice.orders_microservice.models.OrderedProduct;
import com.ordersmicroservice.orders_microservice.services.impl.OrderedProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderedProductController.class)
class OrderedProductControllerTest {
    MockMvc mockMvc;
    @MockBean
    OrderedProductServiceImpl orderedProductService;

    ObjectMapper objectMapper;
    Long orderId1;
    Long orderId2;

    @BeforeEach
    void test(){
        objectMapper = new ObjectMapper();
        OrderedProductController orderedProductController = new OrderedProductController(orderedProductService);
        orderId1 = 1L;
        orderId2 = 2L;
        mockMvc = MockMvcBuilders.standaloneSetup(orderedProductController).build();
    }

    @Test
    @DisplayName("Testing method retrieves all products from the order with id given")
    void testGetAllProductsFromOrder() throws Exception {
        OrderedProduct orderedProduct1 = OrderedProduct
                .builder()
                .productId(1L)
                .quantity(3).build();
        OrderedProduct orderedProduct2 = OrderedProduct
                .builder()
                .productId(2L)
                .quantity(5).build();

        List<OrderedProduct> orderedProducts = Arrays.asList(orderedProduct1, orderedProduct2);
        when(orderedProductService.getAllProductsFromOrder(orderId1)).thenReturn(orderedProducts);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/products/" + orderId1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[1].productId").value(2L))
                .andExpect(jsonPath("$[0].quantity").value(3))
                .andExpect(jsonPath("$[1].quantity").value(5))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(orderedProducts)));
        verify(orderedProductService, times(1)).getAllProductsFromOrder(orderId1);
    }
}
