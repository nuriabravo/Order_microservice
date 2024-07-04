package com.ordersmicroservice.orders_microservice.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("userId")
        private Long userId;
        private List<CartProductDto> cartProducts = new ArrayList<>();
        private BigDecimal totalPrice;
}
