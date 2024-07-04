package com.ordersmicroservice.orders_microservice.services.impl;
import com.ordersmicroservice.orders_microservice.dto.CountryDto;
import com.ordersmicroservice.orders_microservice.services.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Service
public class CountryServiceImpl implements CountryService {
    public String baseUrl;
    public String countryUri;
    private final RestClient restClient;

    public CountryServiceImpl(RestClient restClient,
                              @Value("${users.api.base-url}")String baseUrl,
                              @Value("${users.api.country-uri}")String countryUri) {
        this.baseUrl = baseUrl;
        this.countryUri = countryUri;
        this.restClient = restClient;
    }

    public Optional<CountryDto> getCountryById(Long countryId) {
        log.info("CountryServiceImpl: getCountryById ( countryId = " + countryId + " )");
        return Optional.ofNullable(restClient.get()
                .uri(baseUrl + countryUri, countryId)
                .retrieve()
                .body(CountryDto.class));
    }
}
