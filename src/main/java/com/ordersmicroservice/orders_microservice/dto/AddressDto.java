package com.ordersmicroservice.orders_microservice.dto;
import lombok.Data;

@Data
public class AddressDto {
    private Long id;
    private String cityName;
    private String zipCode;
    private String street;
    private Integer number;
    private String door;
}
