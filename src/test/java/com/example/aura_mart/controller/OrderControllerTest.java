package com.example.aura_mart.controller;

import com.example.aura_mart.dto.OrderDTO;
import com.example.aura_mart.dto.OrderItemDTO;
import com.example.aura_mart.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = OrderController.class,
excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO orderDTO;
    private List<OrderItemDTO> orderItems;

    @BeforeEach
    void setUp() {
        // Create sample order items
        orderItems = Arrays.asList(
                OrderItemDTO.builder()
                        .productId(1L)
                        .quantity(2)
                        .itemPrice(BigDecimal.valueOf(50.00))
                        .build(),
                OrderItemDTO.builder()
                        .productId(2L)
                        .quantity(1)
                        .itemPrice(BigDecimal.valueOf(75.00))
                        .build()
        );

        // Create sample order DTO
        orderDTO = OrderDTO.builder()
                .orderId(1L)
                .userId(100L)
                .totalAmount(BigDecimal.valueOf(175.00))
                .discountedAmount(BigDecimal.valueOf(150.00))
                .status("PENDING")
                .orderDate(LocalDateTime.now())
                .orderItems(orderItems)
                .build();
    }

    @Test
    void createOrder() throws Exception {
        when(orderService.createOrder(any(OrderDTO.class)))
                .thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.userId").value(100L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrderById() throws Exception {
        when(orderService.getOrderById(1L))
                .thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.totalAmount").value(175.00));
    }

    @Test
    void getOrdersByUser() throws Exception {
        List<OrderDTO> userOrders = Arrays.asList(orderDTO);

        when(orderService.getOrdersByUser(100L))
                .thenReturn(userOrders);

        mockMvc.perform(get("/api/orders/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(100L));
    }

    @Test
    void updateOrderStatus() throws Exception {
        OrderDTO updatedOrder = OrderDTO.builder()
                .orderId(1L)
                .status("SHIPPED")
                .build();

        when(orderService.updateOrderStatus(1L, "SHIPPED"))
                .thenReturn(updatedOrder);

        mockMvc.perform(put("/api/orders/1/status")
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void applyDiscount() throws Exception {
        OrderDTO orderWithDiscount = OrderDTO.builder()
                .orderId(1L)
                .discountId(10L)
                .discountedAmount(BigDecimal.valueOf(125.00))
                .build();

        when(orderService.applyDiscount(1L, 10L))
                .thenReturn(orderWithDiscount);

        mockMvc.perform(put("/api/orders/1/discount/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountId").value(10L))
                .andExpect(jsonPath("$.discountedAmount").value(125.00));
    }

    @Test
    void cancelOrder() throws Exception {
        when(orderService.cancelOrder(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void createOrder_InvalidInput() throws Exception {
        OrderDTO invalidOrder = OrderDTO.builder()
                .userId(null) // Violating @NotNull constraint
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isBadRequest());
    }
}