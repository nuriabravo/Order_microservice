package com.ordersmicroservice.orders_microservice.exception;

import lombok.Generated;

@Generated
public class BadRequest extends RuntimeException{
    public BadRequest(String message) {
        super(message);
    }
}