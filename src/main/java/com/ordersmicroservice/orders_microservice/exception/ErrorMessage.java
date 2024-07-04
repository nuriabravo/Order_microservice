package com.ordersmicroservice.orders_microservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Generated
public class ErrorMessage {
    private HttpStatus status;
    private String message;
    private String timestamp;
}

