package com.ordersmicroservice.orders_microservice.services.impl;

import com.ordersmicroservice.orders_microservice.exception.NotFoundException;
import com.ordersmicroservice.orders_microservice.models.OrderedProduct;
import com.ordersmicroservice.orders_microservice.repositories.OrderedProductRepository;
import com.ordersmicroservice.orders_microservice.services.OrderedProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderedProductServiceImpl implements OrderedProductService {
    OrderedProductRepository orderedProductRepository;

    public OrderedProductServiceImpl(OrderedProductRepository orderedProductRepository) {
        this.orderedProductRepository = orderedProductRepository;
    }

    @Override
    public List<OrderedProduct> getAllProductsFromOrder(Long orderId) {
        log.info("OrderedProductServiceImpl: getAllProductsFromOrder ( orderId = " + orderId + " )");
        return Optional.ofNullable(orderedProductRepository.findByOrderId(orderId))
                .filter(orderedProducts -> !orderedProducts.isEmpty())
                .orElseThrow(() -> new NotFoundException("No orders were found for orderId: " + orderId));
    }
}
