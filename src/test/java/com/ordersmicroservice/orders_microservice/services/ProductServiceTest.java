package com.ordersmicroservice.orders_microservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordersmicroservice.orders_microservice.dto.ProductDto;
import com.ordersmicroservice.orders_microservice.services.impl.ProductServiceImpl;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import okhttp3.mockwebserver.MockResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest {
    private MockWebServer mockWebServer;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        productService = new ProductServiceImpl(restClient,
                mockWebServer.url("/").toString(),
                "/products",
                "/products/{id}/{quantity}");
    }

    @Test
    @DisplayName("Testing method updates the stock of a given product")
    void testPatchProductStock() throws Exception {


        ProductDto productDto = ProductDto.builder()
                .id(1L)
                .name("Ball")
                .description("Red Ball")
                .price(15D)
                .categoryId(0L)
                .weight(0D)
                .currentStock(15)
                .minStock(0)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String productJson = objectMapper.writeValueAsString(productDto);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(productJson)
                .addHeader("Content-Type", "application/json"));

        ProductDto updatedProductDto = productService.patchProductStock(1L, -5);

        assertThat(updatedProductDto).isNotNull();
        assertThat(updatedProductDto.getId()).isEqualTo(1L);
        assertThat(updatedProductDto.getName()).isEqualTo("Ball");
        assertThat(updatedProductDto.getCurrentStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("Testing method fails to find the order with id given to be updated")
    void testPatchIdNotFound() {


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("Not found")
                .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> productService.patchProductStock(1L, -5))
                .isInstanceOf(RestClientResponseException.class)
                .hasMessageContaining("Not found")
                .extracting(ex -> ((RestClientResponseException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Testing method gives an Internal Server Error")
    void testPatchServerError() {


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> productService.patchProductStock(1L, -5))
                .isInstanceOf(RestClientResponseException.class)
                .hasMessageContaining("Internal Server Error")
                .extracting(ex -> ((RestClientResponseException) ex).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Testing method retrieves the product with given id")
    void testGetProductById() throws Exception {


        ProductDto productDto = ProductDto.builder()
                .id(1L)
                .name("Ball")
                .description("Red Ball")
                .price(15D)
                .categoryId(0L)
                .weight(0D)
                .currentStock(15)
                .minStock(0)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String productJson = objectMapper.writeValueAsString(productDto);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(productJson)
                .addHeader("Content-Type", "application/json"));

        ProductDto updatedProductDto = productService.getProductById(1L);

        assertThat(updatedProductDto).isNotNull();
        assertThat(updatedProductDto.getId()).isEqualTo(1L);
        assertThat(updatedProductDto.getName()).isEqualTo("Ball");
        assertThat(updatedProductDto.getCurrentStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("Testing method fails to find the product with id given to be retrieved")
    void testGetProductByIdNotFound() {


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("Not found")
                .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> productService.getProductById(1L))
                .isInstanceOf(RestClientResponseException.class)
                .hasMessageContaining("Not found")
                .extracting(ex -> ((RestClientResponseException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Testing method gives an error whenever product with id given is called for")
    void testGetProductByIdError() {


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> productService.getProductById(1L))
                .isInstanceOf(RestClientResponseException.class)
                .hasMessageContaining("Internal Server Error")
                .extracting(ex -> ((RestClientResponseException) ex).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
