package com.ordersmicroservice.orders_microservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordersmicroservice.orders_microservice.dto.*;
import com.ordersmicroservice.orders_microservice.models.Address;
import com.ordersmicroservice.orders_microservice.models.Order;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    private static MockWebServer mockWebServerUser;
    private static MockWebServer mockWebServerCart;
    private static MockWebServer mockWebServerCatalog;



    @BeforeEach
    void beforeEach() throws IOException {

        mockWebServerCart = new MockWebServer();
        mockWebServerCart.start(8081);

        mockWebServerUser = new MockWebServer();
        mockWebServerUser.start(8083);

        mockWebServerCatalog = new MockWebServer();
        mockWebServerCatalog.start(8082);



        System.setProperty("cart.api.base-url", mockWebServerCart.url("/").toString());
        System.setProperty("users.api.base-url", mockWebServerUser.url("/").toString());
        System.setProperty("catalog.api.base-url", mockWebServerCatalog.url("/").toString());

    }

    @AfterEach
    void afterEach() throws IOException {

        mockWebServerCart.shutdown();
        mockWebServerUser.shutdown();
        mockWebServerCatalog.shutdown();

    }

    @Test
    @Disabled
    @DisplayName("Integration Test for Adding an Order Successfully")
    void addOrderIntegrationTest() throws JsonProcessingException {


        ProductDto productDto = ProductDto.builder()
                .id(1L)
                .name("Apple MacBook Pro")
                .description("Latest model of Apple MacBook Pro 16 inch.")
                .price(2399.99D)
                .categoryId(0L)
                .weight(0D)
                .currentStock(15)
                .minStock(0)
                .build();

        ObjectMapper objectMapperProduct = new ObjectMapper();
        String productJson = objectMapperProduct.writeValueAsString(productDto);



        List<CartProductDto> cartProductDtoList = new ArrayList<>();
        CartProductDto cartProductDto1 = CartProductDto.builder()
                .id(1L)
                .productName("Apple MacBook Pro")
                .productDescription("Latest model of Apple MacBook Pro 16 inch.")
                .quantity(1)
                .price(new BigDecimal("2399.99"))
                .build();
        CartProductDto cartProductDto2 = CartProductDto.builder()
                .id(2L)
                .productName("Logitech Mouse")
                .productDescription("Wireless Logitech Mouse M235")
                .price(new BigDecimal("29.99"))
                .quantity(2)
                .build();

        cartProductDtoList.add(cartProductDto1);
        cartProductDtoList.add(cartProductDto2);

        CartDto cartDto = CartDto.builder()
                .id(1L)
                .userId(101L)
                .totalPrice(new BigDecimal("323.3"))
                .cartProducts(cartProductDtoList)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String cartJson = objectMapper.writeValueAsString(cartDto);

        CountryDto countryDto = CountryDto.builder()
                .id(1L)
                .name("España")
                .tax(21F)
                .prefix("+34")
                .timeZone("Europe/Madrid")
                .build();

        ObjectMapper objectMapper3 = new ObjectMapper();
        String countryJson = objectMapper3.writeValueAsString(countryDto);

        Address address = new Address();

        UserDto user = UserDto.builder()
                .id(101L)
                .email("john.doe@example.com")
                .name("John")
                .lastName("Doe")
                .password("password123")
                .fidelityPoints(1000)
                .phone("1234567890")
                .country(countryDto)
                .address(address)
                .build();

        ObjectMapper objectMapper1 = new ObjectMapper();
        String userJson = objectMapper1.writeValueAsString(user);

        mockWebServerCart.enqueue(new MockResponse()
                .setBody(cartJson)
                .addHeader("Content-Type", "application/json"));

        mockWebServerUser.enqueue(new MockResponse()
                .setBody(userJson)
                .addHeader("Content-Type", "application/json"));

        mockWebServerUser.enqueue(new MockResponse()
                .setBody(countryJson)
                .addHeader("Content-Type", "application/json"));

        mockWebServerUser.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));


        mockWebServerCatalog.enqueue(new MockResponse()
                .setBody(productJson)
                .addHeader("Content-Type", "application/json"));

        mockWebServerCatalog.enqueue(new MockResponse()
                .setResponseCode(204)
                .addHeader("Content-Type", "application/json"));

        mockWebServerUser.enqueue(new MockResponse()
                .setResponseCode(204)
                .addHeader("Content-Type", "application/json"));

        Long cartId = 1L;
        CreditCardDto creditCardDto = new CreditCardDto(new BigInteger("1111111111"), "09/25", 222);

        webTestClient.post().uri("/orders/{id}", cartId)
                .bodyValue(creditCardDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Order.class)
                .value(responseOrder -> {
                    AssertionsForClassTypes.assertThat(responseOrder.getUserId()).isEqualTo(101L);
                    AssertionsForClassTypes.assertThat(responseOrder.getTotalPrice()).isEqualTo(new BigDecimal("323.3"));
                    AssertionsForClassTypes.assertThat(responseOrder.getUser().getLastName()).isEqualTo("Doe");
                    AssertionsForClassTypes.assertThat(responseOrder.getStatus()).isEqualTo(Status.PAID);
                    AssertionsForClassTypes.assertThat(responseOrder.getOrderedProducts().get(0).getName()).isEqualTo("Apple MacBook Pro");
                    AssertionsForClassTypes.assertThat(responseOrder.getOrderedProducts().get(1).getName()).isEqualTo("Logitech Mouse");
                });

    }

}