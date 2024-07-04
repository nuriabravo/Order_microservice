package com.ordersmicroservice.orders_microservice;
import com.ordersmicroservice.orders_microservice.dto.CountryDto;
import com.ordersmicroservice.orders_microservice.dto.UserDto;
import com.ordersmicroservice.orders_microservice.dto.UserResponseDto;
import com.ordersmicroservice.orders_microservice.models.Address;
import com.ordersmicroservice.orders_microservice.models.Order;
import com.ordersmicroservice.orders_microservice.models.OrderedProduct;
import lombok.Generated;
import org.apache.tomcat.util.buf.UDecoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ordersmicroservice.orders_microservice.dto.Status.PAID;
import static com.ordersmicroservice.orders_microservice.dto.Status.UNPAID;

@Generated
public class Datos {

    public static List<OrderedProduct> productList = new ArrayList<>();
    static Address address = new Address();
    static Order order = new Order();
    static CountryDto country = crearCountry001();
    static UserResponseDto user = crearUserResponse001();

    public static Optional<Order> crearOrder001(){
        return Optional.of(new Order(1L,1L, 1L,1L,"Valencia",PAID, "2001-21-21","2002-21-21" , user ,crearAddress001().orElseThrow(),country,productList,new BigDecimal("15")));
    }
    public static Optional<Order> crearOrder002() {
        return Optional.of(new Order(2L, 2L, 2L,1L, "Barcelona", UNPAID, "2001-21-21","2002-21-21" ,user, crearAddress002().orElseThrow(), country,productList,new BigDecimal("15")));
    }
    public static Optional<Address> crearAddress001() {
        return Optional.of(new Address(1L, order, "C/ Colon", 10, "5A", "Valencia", "46001", 1L));
    }
    public static Optional<Address> crearAddress002() {
        return Optional.of(new Address(2L, order, "C/ de Navarra", 8, "1B", "Barcelona", "10000", 2L));
    }
    public static UserResponseDto crearUserResponse001(){
        return new UserResponseDto(1L,"Ruben","Marles","ruben@gmail.com","123123123");
    }
    public static UserDto crearUser001(){
        return new UserDto(1L,"Ruben","Marles","ruben@gmail.com","123",12,"123123123",address,country);
    }
    public static CountryDto crearCountry001(){
        return new CountryDto(1L,"Colombia", 20F,"+57","America");
    }
    public static void main(String[] args) {
        productList.add(new OrderedProduct());
        productList.add(new OrderedProduct());
        productList.add(new OrderedProduct());
    }
}
