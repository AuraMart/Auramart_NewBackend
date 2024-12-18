package com.example.aura_mart.controller;

import com.example.aura_mart.dto.OrderItemDTO;
import com.example.aura_mart.dto.PaymentRequestDTO;
import com.example.aura_mart.dto.PaymentResponseDTO;
import com.example.aura_mart.enums.PaymentStatus;
import com.example.aura_mart.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = PaymentController.class,
excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentRequestDTO paymentRequestDTO;
    private PaymentResponseDTO paymentResponseDTO;


    @BeforeEach
    void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Create sample OrderItemDTO
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(1L)
                .quantity(2)
                .itemPrice(BigDecimal.valueOf(50.00))
                .build();

        // Create sample PaymentRequestDTO
        paymentRequestDTO = PaymentRequestDTO.builder()
                .userId(1L)
                .paymentIntentId("pi_test123")
                .paymentMethod("CREDIT_CARD")
                .amount(BigDecimal.valueOf(100.00))
                .orderItems(Collections.singletonList(orderItemDTO))
                .build();

        // Create sample PaymentResponseDTO
        paymentResponseDTO = PaymentResponseDTO.builder()
                .paymentId(1L)
                .orderId(1L)
                .userId(1L)
                .paymentMethod("CREDIT_CARD")
                .paymentStatus(PaymentStatus.COMPLETED)
                .amount(BigDecimal.valueOf(100.00))
                .paymentDate(LocalDateTime.now())
                .stripeTransactionId("ch_test123")
                .build();
    }

    @Test
    void createPaymentIntent() throws Exception {
        String clientSecret = "test_client_secret";

        when(paymentService.createPaymentIntent(1L, BigDecimal.valueOf(100.00)))
                .thenReturn(clientSecret);

        mockMvc.perform(post("/api/payments/create-intent")
                        .param("userId", "1")
                        .param("amount", "100.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

    }



    @Test
    void completePaymentAndCreateOrder() throws Exception {
        when(paymentService.completePaymentAndCreateOrder(any(PaymentRequestDTO.class)))
                .thenReturn(paymentResponseDTO);

        mockMvc.perform(post("/api/payments/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1L))
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"));
    }

    @Test
    void getPaymentById() throws Exception {
        when(paymentService.getPaymentById(1L))
                .thenReturn(paymentResponseDTO);

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1L))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void getPaymentByOrderId() throws Exception {
        when(paymentService.getPaymentByOrderId(1L))
                .thenReturn(paymentResponseDTO);

        mockMvc.perform(get("/api/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"));
    }

    @Test
    void createPaymentIntent_withInvalidParameters() throws Exception {
        mockMvc.perform(post("/api/payments/create-intent")
                        .param("userId", "")
                        .param("amount", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completePaymentAndCreateOrder_withInvalidRequest() throws Exception {
        PaymentRequestDTO invalidRequest = new PaymentRequestDTO();

        mockMvc.perform(post("/api/payments/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}