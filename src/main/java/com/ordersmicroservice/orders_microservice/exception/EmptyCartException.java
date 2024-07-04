package com.ordersmicroservice.orders_microservice.exception;

import lombok.Generated;

@Generated
public class EmptyCartException extends RuntimeException {
    public EmptyCartException(String message) {
        super(message);
    }
}

