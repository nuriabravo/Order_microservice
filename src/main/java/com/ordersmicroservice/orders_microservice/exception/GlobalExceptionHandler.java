package com.ordersmicroservice.orders_microservice.exception;

import lombok.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
@Generated
public class GlobalExceptionHandler {

    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> localNotFoundException(NotFoundException exception) {
        String formattedDate = LocalDateTime.now().format(format);
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage(), formattedDate);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @ExceptionHandler(BadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> localBadRequest(BadRequest exception) {
        String formattedDate = LocalDateTime.now().format(format);
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage(), formattedDate);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorMessage> localServerError(InternalServerErrorException exception) {
        String formattedDate = LocalDateTime.now().format(format);
        ErrorMessage message = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), formattedDate);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String formattedDate = LocalDateTime.now().format(format);
        String errorMessage = "Invalid parameter: " + ex.getName() + ". value '" + ex.getValue() +
                "' cannot be converted";
        ErrorMessage errorResponse = new ErrorMessage(HttpStatus.BAD_REQUEST, errorMessage, formattedDate);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> localBadRequestMinus1InController(HandlerMethodValidationException exception) {
        String formattedDate = LocalDateTime.now().format(format);
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST, "ID must be positive", formattedDate);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(EmptyCartException ex) {
        String formattedDate = LocalDateTime.now().format(format);
        ErrorMessage errorResponse = new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage(), formattedDate);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RetryFailedConnection.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorMessage> localRetryServerError(RetryFailedConnection exception) {
        String formattedDate = LocalDateTime.now().format(format);
        ErrorMessage message = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), formattedDate);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }
}