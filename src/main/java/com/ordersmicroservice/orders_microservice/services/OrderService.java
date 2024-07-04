package com.ordersmicroservice.orders_microservice.services;

import com.ordersmicroservice.orders_microservice.dto.CreditCardDto;
import com.ordersmicroservice.orders_microservice.dto.Status;
import com.ordersmicroservice.orders_microservice.models.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long orderId);
    List<Order> getAllByUserId(Long userId);
    Order createOrder(Long id, CreditCardDto creditCard);
    void deleteById(Long id);
    Order patchOrder(Long id, Status status);

}
