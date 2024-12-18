package com.example.aura_mart.service;

import com.example.aura_mart.dto.PaymentRequestDTO;
import com.example.aura_mart.dto.PaymentResponseDTO;
import com.stripe.exception.StripeException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    // New method to create a payment intent
    String createPaymentIntent(Long userId, BigDecimal amount) throws StripeException;

    // Modified method to complete payment and create order
    PaymentResponseDTO completePaymentAndCreateOrder(PaymentRequestDTO paymentRequestDTO) throws StripeException;

    @Transactional(readOnly = true)
    List<PaymentResponseDTO> getAllPayments();

    PaymentResponseDTO getPaymentById(Long paymentId);
    PaymentResponseDTO getPaymentByOrderId(Long orderId);
}