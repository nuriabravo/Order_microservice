package com.ordersmicroservice.orders_microservice.services;

import com.ordersmicroservice.orders_microservice.dto.*;
import com.ordersmicroservice.orders_microservice.dto.CreditCardDto;
import com.ordersmicroservice.orders_microservice.dto.CartDto;
import com.ordersmicroservice.orders_microservice.dto.CartProductDto;
import com.ordersmicroservice.orders_microservice.dto.Status;
import com.ordersmicroservice.orders_microservice.dto.StatusUpdateDto;
import com.ordersmicroservice.orders_microservice.exception.EmptyCartException;
import com.ordersmicroservice.orders_microservice.exception.NotFoundException;
import com.ordersmicroservice.orders_microservice.models.Address;
import com.ordersmicroservice.orders_microservice.models.Order;
import com.ordersmicroservice.orders_microservice.models.OrderedProduct;
import com.ordersmicroservice.orders_microservice.repositories.OrderRepository;
import com.ordersmicroservice.orders_microservice.services.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.*;

import static com.ordersmicroservice.orders_microservice.Datos.*;
import static com.ordersmicroservice.orders_microservice.dto.Status.IN_DELIVERY;
import static com.ordersmicroservice.orders_microservice.dto.Status.PAID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    OrderRepository orderRepository;
    @InjectMocks
    OrderServiceImpl orderService;
    @Mock
    CartServiceImpl cartService;
    @Mock
    UserServiceImpl userService;
    @Mock
    CountryServiceImpl countryService;
    @Mock
    RestClient restClient;
    @Mock
    AddressServiceImpl addressService;
    @Mock
    ProductServiceImpl productService;
    private Order order1;
    private Order order2;
    private List<Order> orders;
    private UserDto user1;
    private CountryDto country;


    @BeforeEach
    public void setup() {
        this.user1 = crearUser001();
        this.order1 = crearOrder001().orElseThrow();
        this.order2 = crearOrder002().orElseThrow();
        this.country = crearCountry001();
        orders = List.of(order1, order2);
    }


    @Test
    @DisplayName("Testing get all Orders from Repository Method")
    void testGetAllOrders() {

        UserDto user1 = crearUser001();

        CountryDto country = CountryDto.builder()
                .id(1L)
                .name("Colombia")
                .tax(21F)
                .prefix("+57")
                .timeZone("Timezone")
                .build();

        Address address = Address.builder()
                .orderId(1L)
                .cityName("Barranquilla")
                .zipCode("46134")
                .street("Calle 69")
                .number(43)
                .door("2")
                .countryId(1L)
                .build();

        when(orderRepository.findAll()).thenReturn(orders);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user1));
        when(countryService.getCountryById(address.getCountryId())).thenReturn(Optional.ofNullable(country));

        List<Order> savedOrders = orderService.getAllOrders();
        assertThat(savedOrders)
                .isNotNull()
                .isNotEqualTo(Collections.emptyList())
                .isEqualTo(orders);
    }

    @Test
    @DisplayName("Testing get an order by id from repository")
    void testGetOrderById() {

        CountryDto country = CountryDto.builder()
                .id(1L)
                .name("Spain")
                .build();

        this.user1 = UserDto.builder()
                .id(1L)
                .name("Lorenzo")
                .lastName("Perez")
                .email("perez@gmail.com")
                .phone("123123123")
                .country(country)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(userService.getUserById(1L)).thenReturn(Optional.ofNullable(user1));
        when(countryService.getCountryById(1L)).thenReturn(Optional.of(country));

        Order savedOrder = orderService.getOrderById(order1.getId());
        assertThat(savedOrder)
                .isNotNull()
                .isEqualTo(order1);
    }

    @Test
    @DisplayName("Testing get all Orders from Repository Method")
    void testGetAllByUserId() {
        Long userId = 1L;

        Address address = Address.builder()
                .orderId(1L)
                .cityName("Barranquilla")
                .zipCode("46134")
                .street("Calle 69")
                .number(43)
                .door("2")
                .countryId(1L)
                .build();

        CountryDto country = CountryDto.builder()
                .id(1L)
                .name("Colombia")
                .tax(21F)
                .prefix("+57")
                .timeZone("Timezone")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Lorenzo")
                .lastName("Perez")
                .email("perez@gmail.com")
                .phone("123123123")
                .address(address)
                .country(country)
                .build();

        when(userService.getUserById(anyLong())).thenReturn(Optional.ofNullable(userDto));
        when(countryService.getCountryById(address.getCountryId())).thenReturn(Optional.ofNullable(country));
        when(orderRepository.findAllByUserId(userId)).thenReturn(orders);

        List<Order> savedOrders = orderService.getAllByUserId(userId);
        assertThat(savedOrders)
                .isNotNull()
                .isNotEqualTo(Collections.emptyList())
                .isEqualTo(orders);
    }

    @Test
    @DisplayName("Testing Adding a new order with just an id")
    void testAddOrder() {
        String[] addresses = {"123 Main St", "456 Elm St", "789 Oak St", "101 Maple Ave", "222 Pine St", "333 Cedar Rd"};

        CreditCardDto creditCard = CreditCardDto.builder()
                .cardNumber(new BigInteger("1234567812345678"))
                .expirationDate("12/25")
                .cvcCode(123)
                .build();

        Long cartId = 1L;
        Long user_id = 1L;
        BigDecimal totalPrice = new BigDecimal("100.00");
        List<CartProductDto> cartProducts = List.of(
                new CartProductDto(1L, "Product1", "Description1", 2, new BigDecimal("20.00")),
                new CartProductDto(2L, "Product2", "Description2", 1, new BigDecimal("30.00"))
        );

        CartDto cartDto = CartDto.builder()
                .userId(user_id)
                .cartProducts(cartProducts)
                .totalPrice(totalPrice)
                .build();

        Address address = Address.builder()
                .orderId(1L)
                .cityName("Barranquilla")
                .zipCode("46134")
                .street("Calle 69")
                .number(43)
                .door("2")
                .countryId(1L)
                .build();

        CountryDto country = CountryDto.builder()
                .id(1L)
                .name("Colombia")
                .tax(21F)
                .prefix("+57")
                .timeZone("Timezone")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Lorenzo")
                .lastName("Perez")
                .email("perez@gmail.com")
                .phone("123123123")
                .address(address)
                .country(country)
                .build();

        when(cartService.getCartById(cartId)).thenReturn(Optional.ofNullable(cartDto));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userService.getUserById(cartDto.getUserId())).thenReturn(Optional.ofNullable(userDto));
        when(countryService.getCountryById(address.getCountryId())).thenReturn(Optional.ofNullable(country));


        RestClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(RestClient.RequestBodyUriSpec.class);

        Order expectedOrder = Order.builder()
                .totalPrice(new BigDecimal("100.00"))
                .address(address)
                .status(PAID)
                .dateOrdered("2024-02-10")
                .orderedProducts(Arrays.asList(
                        OrderedProduct.builder()
                                .productId(cartProducts.get(0).getId())
                                .name(cartProducts.get(0).getProductName())
                                .description(cartProducts.get(0).getProductDescription())
                                .price(cartProducts.get(0).getPrice())
                                .quantity(cartProducts.get(0).getQuantity())
                                .build(),
                        OrderedProduct.builder()
                                .productId(cartProducts.get(1).getId())
                                .name(cartProducts.get(1).getProductName())
                                .description(cartProducts.get(1).getProductDescription())
                                .price(cartProducts.get(1).getPrice())
                                .quantity(cartProducts.get(1).getQuantity())
                                .build()
                ))
                .build();

        Order savedOrder = orderService.createOrder(cartId, creditCard);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getTotalPrice()).isEqualTo(totalPrice);
        assertThat(savedOrder.getAddress()).isEqualTo(address);
        assertThat(savedOrder.getStatus()).isEqualTo(PAID);
        assertThat(savedOrder.getDateOrdered()).isNotNull();
        assertThat(savedOrder.getDateDelivered()).isNull();

        List<OrderedProduct> orderedProducts = savedOrder.getOrderedProducts();
        assertThat(orderedProducts).isNotNull().hasSameSizeAs(cartProducts);

        for (int i = 0; i < cartProducts.size(); i++) {
            CartProductDto cartProduct = cartProducts.get(i);
            OrderedProduct orderedProduct = orderedProducts.get(i);

            assertThat(orderedProduct.getProductId()).isEqualTo(cartProduct.getId());
            assertThat(orderedProduct.getName()).isEqualTo(cartProduct.getProductName());
            assertThat(orderedProduct.getDescription()).isEqualTo(cartProduct.getProductDescription());
            assertThat(orderedProduct.getPrice()).isEqualTo(cartProduct.getPrice());
            assertThat(orderedProduct.getQuantity()).isEqualTo(cartProduct.getQuantity());
        }
    }

    @Test
    @DisplayName("Test addOrder when cart is not found")
    void testAddOrderCartNotFound() {
        Long cartId = 1L;
        when(cartService.getCartById(cartId)).thenReturn(Optional.empty());


        CreditCardDto creditCardDto = new CreditCardDto();
        assertThatThrownBy(() -> orderService.createOrder(cartId, creditCardDto))
                .isInstanceOf(NotFoundException.class);

        verify(cartService, times(1)).getCartById(cartId);
    }

    @Test
    @DisplayName("Test addOrder when cart is empty")
    void testAddOrderEmptyCart() {
        Long cartId = 1L;
        CartDto emptyCart = new CartDto();
        emptyCart.setCartProducts(Collections.emptyList());
        when(cartService.getCartById(cartId)).thenReturn(Optional.of(emptyCart));

        CreditCardDto creditCardDto = new CreditCardDto();
        assertThatThrownBy(() -> orderService.createOrder(cartId, creditCardDto))
                .isInstanceOf(EmptyCartException.class);

        verify(cartService, times(1)).getCartById(cartId);
    }

    @Test
    @DisplayName("Testing the update of an order")
    void testPatchOrderIfFound() {
        Order existingOrder = Order.builder()
                .id(order1.getId())
                .status(order1.getStatus())
                .dateDelivered(order1.getDateDelivered())
                .orderedProducts(new ArrayList<>())
                .userId(1L)
                .countryId(1L)
                .build();

        StatusUpdateDto statusUpdateDto = new StatusUpdateDto();
        statusUpdateDto.setStatus(Status.CANCELLED);

        UserDto user1 = crearUser001();

        CountryDto country = CountryDto.builder()
                .id(1L)
                .name("Colombia")
                .tax(21F)
                .prefix("+57")
                .timeZone("Timezone")
                .build();

        Address address = Address.builder()
                .orderId(1L)
                .cityName("Barranquilla")
                .zipCode("46134")
                .street("Calle 69")
                .number(43)
                .door("2")
                .countryId(1L)
                .build();

        when(orderRepository.findById(order1.getId())).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(countryService.getCountryById(address.getCountryId())).thenReturn(Optional.ofNullable(country));

        Order patchedOrder = orderService.patchOrder(order1.getId(), statusUpdateDto.getStatus());

        assertThat(patchedOrder.getStatus()).isEqualTo(Status.CANCELLED);

        verify(orderRepository, times(1)).findById(order1.getId());
        verify(orderRepository, times(1)).save(existingOrder);
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("Testing patching an order with DELIVERED status")
    void testPatchOrderDelivered() {
        Order initialOrder = order1;
        initialOrder.setStatus(IN_DELIVERY);
        initialOrder.setOrderedProducts(new ArrayList<>(List.of(
                new OrderedProduct(),
                new OrderedProduct()
        )));

        CountryDto country2 =  CountryDto.builder()
                .id(1L)
                .name("Country 1")
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(initialOrder));
        when(orderRepository.save(initialOrder)).thenReturn(initialOrder);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(countryService.getCountryById(1L)).thenReturn(Optional.of(country2));

        Order patchedOrder = orderService.patchOrder(1L, Status.DELIVERED);

        assertThat(patchedOrder).isNotNull();
        assertThat(patchedOrder.getStatus()).isEqualTo(Status.DELIVERED);
        assertThat(patchedOrder.getDateDelivered()).isNotNull();
    }

    @Test
    @DisplayName("Testing the update when order is not found")
    void testPatchOrderIfNotFound() {
        StatusUpdateDto statusUpdateDto = new StatusUpdateDto();
        statusUpdateDto.setStatus(Status.CANCELLED);

        when(orderRepository.findById(order1.getId())).thenReturn(Optional.empty());

        String message = "Order not found with ID: " + order1.getId();

        Status status = statusUpdateDto.getStatus();
        Long order1Id = order1.getId();

        assertThatThrownBy(() -> orderService.patchOrder(order1Id, status))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(message);

        verify(orderRepository, times(1)).findById(order1.getId());
        verify(orderRepository, times(0)).save(any(Order.class));
    }

    @Test
    @DisplayName("Testing patching an order with RETURNED status")
    void testPatchOrderReturned() {
        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        OrderedProduct product1 = OrderedProduct.builder()
                .productId(1L)
                .quantity(5)
                .build();

        OrderedProduct product2 = OrderedProduct.builder()
                .productId(2L)
                .quantity(3)
                .build();

        Order initialOrder = Order.builder()
                .id(1L)
                .status(IN_DELIVERY)
                .userId(1L)
                .countryId(1L)
                .orderedProducts(Arrays.asList(product1, product2))
                .build();

        CountryDto country2 = CountryDto.builder()
                .id(1L)
                .name("Country 1")
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(initialOrder));
        when(orderRepository.save(initialOrder)).thenReturn(initialOrder);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(countryService.getCountryById(1L)).thenReturn(Optional.of(country2));

        Order patchedOrder = orderService.patchOrder(1L, Status.RETURNED);

        assertThat(patchedOrder).isNotNull();
        assertThat(patchedOrder.getStatus()).isEqualTo(Status.RETURNED);
    }

    @Test
    @DisplayName("Testing the deleting of an order")
    void testDeleteById() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order()));

        orderService.deleteById(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    @DisplayName("Testing the deleting of an order if the order with the given id is not found")
    void testDeleteByIdNotFound() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deleteById(orderId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Order with ID " + orderId + " not found.");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).deleteById(orderId);
    }

    @Test
    @DisplayName("Testing getAllOrders when no orders exist")
    void testGetAllOrdersNoOrdersFound() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> orderService.getAllOrders())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No orders were found");

        verify(orderRepository).findAll();
    }

}
