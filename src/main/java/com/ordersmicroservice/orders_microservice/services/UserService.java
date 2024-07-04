package com.ordersmicroservice.orders_microservice.services;

import com.ordersmicroservice.orders_microservice.dto.UserDto;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> getUserById(Long userId);
    void patchFidelityPoints(Long userId, int points);
}
