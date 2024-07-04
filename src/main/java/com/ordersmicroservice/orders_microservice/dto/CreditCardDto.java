package com.ordersmicroservice.orders_microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardDto {
    private BigInteger cardNumber;
    private String expirationDate;
    private int cvcCode;
}
