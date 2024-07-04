package com.ordersmicroservice.orders_microservice.services;

import com.ordersmicroservice.orders_microservice.models.OrderedProduct;

import java.util.List;

public interface OrderedProductService {
    List<OrderedProduct> getAllProductsFromOrder(Long orderId);

}
