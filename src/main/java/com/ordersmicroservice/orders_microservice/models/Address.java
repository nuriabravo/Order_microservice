package com.ordersmicroservice.orders_microservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @Column(name = "order_id")
    private Long orderId;

    @JsonBackReference
    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    @EqualsAndHashCode.Exclude
    private Order order;

    @Column(name = "street")
    private String street;

    @Column(name = "number")
    private Integer number;

    @Column(name = "door")
    private String door;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "country_id")
    private Long countryId;

}
