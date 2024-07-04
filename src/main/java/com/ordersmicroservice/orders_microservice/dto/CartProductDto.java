package com.ordersmicroservice.orders_microservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDto {

    @Id
    @JsonProperty("productId")
    private Long id;
    private String productName;
    private String productDescription;
    private Integer quantity;
    private BigDecimal price;

}
