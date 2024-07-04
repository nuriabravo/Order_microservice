package com.ordersmicroservice.orders_microservice.services;

import com.ordersmicroservice.orders_microservice.dto.CartDto;

import java.util.Optional;

public interface CartService {

    Optional<CartDto> getCartById(Long id);
    void emptyCartProductsById(Long id);
}
