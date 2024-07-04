package com.ordersmicroservice.orders_microservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordersmicroservice.orders_microservice.dto.CreditCardDto;
import com.ordersmicroservice.orders_microservice.dto.Status;
import com.ordersmicroservice.orders_microservice.dto.StatusUpdateDto;
import com.ordersmicroservice.orders_microservice.exception.NotFoundException;
import com.ordersmicroservice.orders_microservice.models.Order;
import com.ordersmicroservice.orders_microservice.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static com.ordersmicroservice.orders_microservice.Datos.crearOrder001;
import static com.ordersmicroservice.orders_microservice.Datos.crearOrder002;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    MockMvc mockMvc;
    @MockBean
    OrderService orderService;
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        OrderController orderController = new OrderController(orderService);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    @DisplayName("Testing method retrieves all orders from the endpoint")
    void testGetAllOrders() throws Exception {
        List<Order> mockOrders = Arrays.asList(crearOrder001().orElseThrow(),
                crearOrder002().orElseThrow());
        when(orderService.getAllOrders()).thenReturn(mockOrders);

        mockMvc.perform(get("/orders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].status").value("PAID"))
                .andExpect(jsonPath("$[1].status").value("UNPAID"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(mockOrders)));

        verify(orderService).getAllOrders();
    }

    @Test
    @DisplayName("Testing method retrieves order with given id from the endpoint")
    void testGetOrderById() throws Exception {
        Long id = 1L;
        when(orderService.getOrderById(1L)).thenReturn(crearOrder001().orElseThrow());

        //When
        mockMvc.perform(get("/orders/{id}", id).contentType(MediaType.APPLICATION_JSON))

                //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PAID"));

        verify(orderService).getOrderById(1L);
    }

    @Test
    @DisplayName("Testing method retrieves all orders from the endpoint")
    void testGetAllByUserId() throws Exception {
        Long userId = crearOrder001().orElseThrow().getUserId();

        List<Order> mockOrders = Arrays.asList(crearOrder001().orElseThrow(),
                crearOrder002().orElseThrow());
        when(orderService.getAllByUserId(userId)).thenReturn(mockOrders);

        mockMvc.perform(get("/orders/user/{$id}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PAID"))
                .andExpect(content().json(objectMapper.writeValueAsString(mockOrders)));

        verify(orderService).getAllByUserId(userId);
    }
    @Test
    @DisplayName("Testing method posts a new order to the endpoint")
    void testPostNewOrder() throws Exception {

        Long cartId = 1L;

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setCardNumber(new BigInteger("1234567812345678"));
        creditCardDto.setExpirationDate("12/25");
        creditCardDto.setCvcCode(123);
        String creditCardJson = objectMapper.writeValueAsString(creditCardDto);

        when(orderService.createOrder(cartId,creditCardDto)).thenAnswer(invocation -> Order
                .builder()
                .cartId(cartId)
                .fromAddress("Madrid")
                .status(Status.DELIVERED)
                .dateOrdered("2001-01-21")
                .dateDelivered("2002-01-21")
                .build());

        mockMvc.perform(post("/orders/{id}", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creditCardJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartId", is(cartId.intValue())))
                .andExpect(jsonPath("$.fromAddress", is("Madrid")))
                .andExpect(jsonPath("$.dateOrdered", is("2001-01-21")));

        verify(orderService).createOrder(cartId,creditCardDto);
    }

    @Test
    @DisplayName("Testing method deletes order with given id from the endpoint")
    void testDeleteById() throws Exception {

        Long id = 3L;

        mockMvc.perform(delete("/orders/{id}", id))
                .andExpect(status().isOk());

        verify(orderService).deleteById(id);

    }

    @Test
    @DisplayName("Testing method fails to find the order with id given to be deleted")
    void testDeleteByIdShouldFailWhenIdNotFound(){
        Long id = 33L;
        doThrow(new NotFoundException("Order not found")).when(orderService).deleteById(id);

        assertThrows(NotFoundException.class, () -> orderService.deleteById(id));
        verify(orderService).deleteById(id);
    }
    @Test
    @DisplayName("Testing method updates the order with given id from the endpoint")
    void testPatchOrder () throws Exception {

        Long id = 1L;
        StatusUpdateDto statusUpdateDto = new StatusUpdateDto();
        statusUpdateDto.setStatus(Status.PAID);

        Order mockOrder = new Order();
        mockOrder.setId(id);
        mockOrder.setStatus(Status.PAID);

        when(orderService.patchOrder(eq(id), any(Status.class))).thenReturn(mockOrder);

        mockMvc.perform(patch("/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(orderService).patchOrder(id, statusUpdateDto.getStatus());
    }


}
