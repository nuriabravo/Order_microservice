package com.ordersmicroservice.orders_microservice.services.impl;

import com.ordersmicroservice.orders_microservice.dto.UserDto;
import com.ordersmicroservice.orders_microservice.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    public String baseUrl;
    public String usersUri;
    public String fidelityUri;
    private final RestClient restClient;

    public UserServiceImpl(RestClient restClient,
                           @Value("${users.api.base-url}")String baseUrl,
                           @Value("${users.api.users-uri}")String usersUri,
                           @Value("${users.api.fidelity-uri}")String fidelityUri) {
        this.baseUrl = baseUrl;
        this.usersUri = usersUri;
        this.fidelityUri = fidelityUri;
        this.restClient = restClient;
    }

    public Optional<UserDto> getUserById(Long userId) {
        log.info("getUserById( userId:  {} )",userId);
        return Optional.ofNullable(restClient.get()
                .uri(baseUrl + usersUri, userId)
                .retrieve()
                .body(UserDto.class));
    }

    public void patchFidelityPoints(Long userId, int points){
        restClient.patch()
                .uri(baseUrl + fidelityUri, userId)
                .body(points)
                .header("Content-Type", "application/json")
                .retrieve();
    }
}