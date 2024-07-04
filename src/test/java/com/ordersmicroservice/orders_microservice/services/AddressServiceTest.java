package com.ordersmicroservice.orders_microservice.services;

import com.ordersmicroservice.orders_microservice.models.Address;
import com.ordersmicroservice.orders_microservice.repositories.AddressRepository;
import com.ordersmicroservice.orders_microservice.services.impl.AddressServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {


    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void testSaveAddress() {

        Address address = Address.builder()
                .orderId(1L)
                .street("Main St")
                .number(123)
                .door("1A")
                .cityName("Sample City")
                .zipCode("12345")
                .countryId(1L)
                .build();

        when(addressRepository.save(any(Address.class))).thenReturn(address);


        Address savedAddress = addressService.saveAddress(address);


        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getOrderId()).isEqualTo(1L);
        assertThat(savedAddress.getStreet()).isEqualTo("Main St");
        assertThat(savedAddress.getCityName()).isEqualTo("Sample City");

        verify(addressRepository).save(any(Address.class));
    }
 }
