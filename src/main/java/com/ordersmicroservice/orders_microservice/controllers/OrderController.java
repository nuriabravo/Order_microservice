package com.ordersmicroservice.orders_microservice.controllers;

import com.ordersmicroservice.orders_microservice.dto.CreditCardDto;
import com.ordersmicroservice.orders_microservice.dto.StatusUpdateDto;
import com.ordersmicroservice.orders_microservice.models.Order;
import com.ordersmicroservice.orders_microservice.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {
    OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @ResponseStatus(OK)
    @Operation(summary = "List all Orders", description = "This endpoint retrieves example data from the server.")
    public ResponseEntity<List<Order>> getAllOrders() {
            log.info("GET: getAllOrders");
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Order by ID", description = "This endpoint retrieves example data by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Order> getOrderById(@PathVariable @Positive Long id) {
            log.info("GET: getOrderById ( id = " + id + " )");
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(OK)
    @Operation(summary = "List all Orders pertaining to a user", description = "This endpoint retrieves example data by User ID.")
    public ResponseEntity<List<Order>> getAllByUserId(@PathVariable Long userId) {
        log.info("GET: getAllByUserId ( id = " + userId + " )");
        List<Order> orders = orderService.getAllByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{id}")
    @ResponseStatus(CREATED)
    @Operation(summary = "Create a new order", description = "This endpoint retrieves example data from the server.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order Created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Order> postOrder(@PathVariable @Positive Long id, @RequestBody CreditCardDto creditCart){
        log.info("POST: postOrder( id = " + id + " " + creditCart.getCardNumber() + " )");
        Order order = orderService.createOrder(id,creditCart);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an order", description = "This endpoint retrieves example data from the server.")
    public ResponseEntity<String> deleteById(@PathVariable @Positive Long id) {
        log.info("DELETE: deleteById ( " + id + " )");
        orderService.deleteById(id);
        return ResponseEntity.status(OK).body("Order with id " + id + " has been deleted successfully");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an order", description = "This endpoint updates the status of an order based on the provided ID.")
    public ResponseEntity<Order> patchOrder(@PathVariable @Positive Long id, @RequestBody StatusUpdateDto patchData) {
        log.info("PATCH: patchOrder ( " + id + " )");
        Order updatedOrder = orderService.patchOrder(id, patchData.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }
}
