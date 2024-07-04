package com.ordersmicroservice.orders_microservice.models;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ordersmicroservice.orders_microservice.dto.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "country_id")
    private Long countryId;

    @Column(name="from_address")
    private String fromAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "date_ordered")
    private String dateOrdered;

    @Column(name = "date_delivered")
    private String dateDelivered;

    @Transient
    private UserResponseDto user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    private Address address;

    @Transient
    private CountryDto country;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderedProduct> orderedProducts;

    @Column(name = "total_price")
    private BigDecimal totalPrice;
}
