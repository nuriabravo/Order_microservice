package com.ordersmicroservice.orders_microservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordersmicroservice.orders_microservice.dto.CountryDto;
import com.ordersmicroservice.orders_microservice.dto.UserDto;
import com.ordersmicroservice.orders_microservice.models.Address;
import com.ordersmicroservice.orders_microservice.services.impl.UserServiceImpl;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserServiceTest {
    private MockWebServer mockWebServer;

    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        userServiceImpl = new UserServiceImpl(restClient,
                mockWebServer.url("/").toString(),
                "/users/{userId}",
        "/fidelitypoints/{id}");

    }

    @Test
    @DisplayName("When fetching a user by ID, then the correct user details are returned")
    void testGetUserById() throws Exception {


        String userJson = buildUser();
        mockWebServer.enqueue(new MockResponse()
                .setBody(userJson)
                .addHeader("Content-Type", "application/json"));

        UserDto retrievedUserDto = userServiceImpl.getUserById(100L).orElseThrow()
                ;

        assertThat(retrievedUserDto).isNotNull();
        assertThat(retrievedUserDto.getId()).isEqualTo(100L);
        assertThat(retrievedUserDto.getName()).isEqualTo("John");
        assertThat(retrievedUserDto.getLastName()).isEqualTo("Doe");
        assertThat(retrievedUserDto.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(retrievedUserDto.getPhone()).isEqualTo("1234567890");
        assertThat(retrievedUserDto.getFidelityPoints()).isEqualTo(1000);
        assertThat(retrievedUserDto.getAddress().getCityName()).isEqualTo("Madrid");
        assertThat(retrievedUserDto.getAddress().getZipCode()).isEqualTo("47562");
        assertThat(retrievedUserDto.getAddress().getStreet()).isEqualTo("C/ La Coma");
        assertThat(retrievedUserDto.getAddress().getNumber()).isEqualTo(32);
        assertThat(retrievedUserDto.getAddress().getDoor()).isEqualTo("1A");
        assertThat(retrievedUserDto.getCountry().getId()).isEqualTo(1L);
        assertThat(retrievedUserDto.getCountry().getName()).isEqualTo("España");
        assertThat(retrievedUserDto.getCountry().getTax()).isEqualTo(21);
        assertThat(retrievedUserDto.getCountry().getPrefix()).isEqualTo("+34");
        assertThat(retrievedUserDto.getCountry().getTimeZone()).isEqualTo("Europe/Madrid");
    }

    private static String buildUser() throws JsonProcessingException {
        Address address = Address.builder()
                .cityName("Madrid")
                .zipCode("47562")
                .street("C/ La Coma")
                .number(32)
                .door("1A")
                .build();

        CountryDto countryDto = CountryDto.builder()
                .id(1L)
                .name("España")
                .tax(21F)
                .prefix("+34")
                .timeZone("Europe/Madrid")
                .build();

        UserDto userDto = UserDto.builder()
                .id(100L)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .fidelityPoints(1000)
                .phone("1234567890")
                .address(address)
                .country(countryDto)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userDto);
    }

    @Test
    @DisplayName("When fetching a non-existent user by ID, then a 404 error is returned")
    void testGetUserByIdNotFound() {



        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("User not found")
                .addHeader("Content-Type", "text/plain"));

        assertThatThrownBy(() -> userServiceImpl.getUserById(1L))
                .isInstanceOf(RestClientResponseException.class)
                .hasMessageContaining("User not found")
                .extracting(ex -> ((RestClientResponseException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("When fetching a User by ID and an internal server error occurs, then a 500 error is returned")
    void testGetProductByIdServerError() {


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "text/plain"));

        assertThatThrownBy(() -> userServiceImpl.getUserById(1L))
                .isInstanceOf(RestClientResponseException.class)
                .hasMessageContaining("Internal Server Error")
                .extracting(ex -> ((RestClientResponseException) ex).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Successfully patches user fidelity points")
    void testPatchFidelityPointsSuccess() throws InterruptedException, IOException {

        int points = 500;

        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"message\":\"success\"}")
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));


        assertDoesNotThrow(() -> userServiceImpl.patchFidelityPoints(100L, points));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("PATCH");
        assertThat(recordedRequest.getPath()).isEqualTo("/fidelitypoints/100");
        assertThat(recordedRequest.getBody().readUtf8()).isEqualTo(String.valueOf(points));


        assertThat(recordedRequest.getHeader("Content-Type")).isEqualTo("application/json");

    }


    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}
