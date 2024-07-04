package com.ordersmicroservice.orders_microservice.services.impl;

import com.ordersmicroservice.orders_microservice.dto.*;
import com.ordersmicroservice.orders_microservice.dto.CreditCardDto;
import com.ordersmicroservice.orders_microservice.dto.CartDto;
import com.ordersmicroservice.orders_microservice.dto.Status;
import com.ordersmicroservice.orders_microservice.exception.EmptyCartException;
import com.ordersmicroservice.orders_microservice.exception.NotFoundException;
import com.ordersmicroservice.orders_microservice.models.Address;
import com.ordersmicroservice.orders_microservice.models.Order;
import com.ordersmicroservice.orders_microservice.models.OrderedProduct;
import com.ordersmicroservice.orders_microservice.repositories.OrderRepository;
import com.ordersmicroservice.orders_microservice.services.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.ordersmicroservice.orders_microservice.dto.Status.PAID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    Random random;
    CartService cartService;
    UserService userService;
    AddressService addressService;
    CountryService countryService;
    RestClient restClient;

    ProductServiceImpl productService;

    public OrderServiceImpl(OrderRepository orderRepository, CartService cartService, UserService userService, AddressService addressService, CountryService countryService, RestClient restClient, ProductServiceImpl productService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.addressService = addressService;
        this.countryService = countryService;
        this.restClient = restClient;
        this.productService = productService;
    }

    @Override
    public List<Order> getAllOrders() {
        log.info("Getting All Orders by ID in service");
         List<Order> ordersList = Optional.of(orderRepository.findAll()).filter(orders -> !orders.isEmpty())
                .orElseThrow(() -> new NotFoundException("No orders were found"));
        ordersList.forEach(this::setCountryAndUserToOrder);
        return ordersList;
    }

    @Override
    public Order getOrderById(Long orderId) {
        log.info("Getting Order by ID: {}",orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        setCountryAndUserToOrder(order);
        return order;
    }

    private void setCountryAndUserToOrder(Order order) {
        log.info("Setting Country and User to Order: {}",order.getId());
        UserDto user = userService.getUserById(order.getUserId()).orElseThrow(() -> new NotFoundException("User not found with ID: " + order.getUserId()));
        UserResponseDto userResponse = UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
        CountryDto countryDto = countryService.getCountryById(order.getCountryId()).orElseThrow();
        order.setCountry(countryDto);
        order.setUser(userResponse);
    }

    @Override
    public List<Order> getAllByUserId(Long userId) {
        log.info("Getting All Orders by UserID: {}",userId);
        List<Order> ordersList = orderRepository.findAllByUserId(userId);
        ordersList.forEach(this::setCountryAndUserToOrder);
        return ordersList;
    }

    @Override
    public Order createOrder(Long cartId, CreditCardDto creditCard) {
        log.info("Sending credit card info to payment Server...");
        log.info("Payment with the credit card " + creditCard.getCardNumber() + " has been made successfully" );

        CartDto cart = checkCartAndCartProducts(cartId);

        UserDto user = getUserFromCart(cart, cartId);
        UserResponseDto userResponse = UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();

        Order order = Order.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .fromAddress(randomAddress())
                .status(PAID)
                .dateOrdered(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .user(userResponse)
                .totalPrice(cart.getTotalPrice())
                .build();


       order = orderRepository.save(order);

        List<OrderedProduct> orderedProducts = getOrderedProductsListFromCart(cart, order);

        order.setOrderedProducts(orderedProducts);

        configureCountryAndAddress(order, user);
        
        updateStockForOrderedProducts(orderedProducts);

        userService.patchFidelityPoints(order.getUserId(), fidelityPoints(order.getTotalPrice()));

        cartService.emptyCartProductsById(cartId);

        return order;
    }

    private List<OrderedProduct> getOrderedProductsListFromCart(CartDto cart, Order order) {
        List<CartProductDto> cartProducts = cart.getCartProducts();

        return new ArrayList<>(cartProducts.stream()
                .map(cartProductDto -> convertToOrderedProduct(cartProductDto, order))
                .toList());
    }

    private OrderedProduct convertToOrderedProduct(CartProductDto cartProductDto, Order order) {
        return OrderedProduct.builder()
                .order(order)
                .productId(cartProductDto.getId())
                .name(cartProductDto.getProductName())
                .description(cartProductDto.getProductDescription())
                .price(cartProductDto.getPrice())
                .quantity(cartProductDto.getQuantity())
                .build();
    }

    private void updateStockForOrderedProducts(List<OrderedProduct> orderedProducts) {
        orderedProducts.forEach(orderedProduct -> productService.patchProductStock(orderedProduct.getProductId(), orderedProduct.getQuantity() * (-1)));
    }

    private void configureCountryAndAddress(Order order, UserDto user) {
        log.info("Configure Country and Address of the userID: {}", user.getId());
        CountryDto country = countryService.getCountryById(user.getCountry().getId())
                .orElseThrow(() -> new NotFoundException("Country not found with ID: " + user.getCountry().getId()));

        Address address = user.getAddress();
        address.setCountryId(user.getCountry().getId());
        address.setOrder(order);
        order.setCountryId(user.getCountry().getId());
        order.setCountry(country);
        order.setAddress(address);
        addressService.saveAddress(address);
    }

    private UserDto getUserFromCart(CartDto cart, Long cartId) {
        return userService.getUserById(cart.getUserId()).orElseThrow(() -> new NotFoundException("User not found with ID: " + cartId));
    }

    private CartDto checkCartAndCartProducts(Long cartId) {
        CartDto cart = cartService.getCartById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));

        if (cart.getCartProducts().isEmpty()) {
            throw new EmptyCartException("Empty cart, order not made");
        }
        return cart;
    }

    private String randomAddress() {
        String[] addresses = {"123 Main St", "456 Elm St", "789 Oak St", "101 Maple Ave", "222 Pine St", "333 Cedar Rd"};
        this.random = new Random();
        return addresses[this.random.nextInt(addresses.length)];
    }

    @Override
    @Transactional
    public Order patchOrder(Long id, @RequestBody Status updatedStatus) {
        log.info("Update Order Status UserID: {}",id);
        log.info("User Notification : Change Status to {}", updatedStatus);
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id));
        existingOrder.setStatus(updatedStatus);

        Map<Status, Consumer<Order>> statusActions = Map.of(
                Status.DELIVERED, this::handleDeliveredStatus,
                Status.RETURNED, this::handleReturnedStatus,
                Status.CANCELLED, this::handleReturnedStatus
        );

        statusActions.getOrDefault(updatedStatus, order -> {

        }).accept(existingOrder);

        setCountryAndUserToOrder(existingOrder);
        return orderRepository.save(existingOrder);
    }

    private void handleDeliveredStatus(Order order) {
        order.setDateDelivered(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private void handleReturnedStatus(Order order) {
        log.info("User Notification : Making the refund of the payment of {} ...", order.getTotalPrice());

        for (OrderedProduct orderedProduct : order.getOrderedProducts()) {
            productService.patchProductStock(orderedProduct.getProductId(),orderedProduct.getQuantity());
        }
        userService.patchFidelityPoints(order.getUserId(), fidelityPoints(order.getTotalPrice()) *(-1));
    }

    private Integer fidelityPoints(BigDecimal totalPrice) {
        if (totalPrice == null) {
            return 0;
        }
        return Stream.of(
                        totalPrice.floatValue() <= 20 ? 0 : null,
                        totalPrice.floatValue() > 20 && totalPrice.floatValue() <= 30 ? 1 : null,
                        totalPrice.floatValue() > 30 && totalPrice.floatValue() <= 50 ? 3 : null,
                        totalPrice.floatValue() > 50 && totalPrice.floatValue() <= 100 ? 5 : null,
                        totalPrice.floatValue() > 100 ? 10 : null
                )
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(0);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting order by ID: {}",id);
        orderRepository.findById(id)
                .ifPresentOrElse(
                        order -> orderRepository.deleteById(id),
                        () -> {
                            throw new NotFoundException("Order with ID " + id + " not found.");
                        }
                );
    }
}


