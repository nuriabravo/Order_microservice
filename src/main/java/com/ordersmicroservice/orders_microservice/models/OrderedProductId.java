package com.ordersmicroservice.orders_microservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class OrderedProductId implements Serializable {
    private Long orderId; //NOSONAR Necessary for SQL
    private Long productId; //NOSONAR Necessary for SQL;
}