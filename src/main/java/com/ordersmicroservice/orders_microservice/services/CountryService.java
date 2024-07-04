package com.ordersmicroservice.orders_microservice.services;

import com.ordersmicroservice.orders_microservice.dto.CountryDto;

import java.util.Optional;

public interface CountryService {
    Optional<CountryDto> getCountryById(Long countryId);
}
