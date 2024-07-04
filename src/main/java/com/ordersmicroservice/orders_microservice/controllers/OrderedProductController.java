package com.ordersmicroservice.orders_microservice.controllers;

import com.ordersmicroservice.orders_microservice.models.OrderedProduct;
import com.ordersmicroservice.orders_microservice.services.impl.OrderedProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/orders/products")
public class OrderedProductController {
    OrderedProductServiceImpl orderedProductService;
    public OrderedProductController(OrderedProductServiceImpl orderedProductService){
        this.orderedProductService = orderedProductService;
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(OK)
    @Operation(summary = "List all products from an order", description = "This endpoint retrieves example data from the server.")
    public ResponseEntity<List<OrderedProduct>> getAllProductsFromOrder(@PathVariable("orderId") Long orderId){
        log.info("GET: getAllProductsFromOrder ( " + orderId + " )");
        List<OrderedProduct> orderedProduct = orderedProductService.getAllProductsFromOrder(orderId);
        return ResponseEntity.ok(orderedProduct);
    }
}
