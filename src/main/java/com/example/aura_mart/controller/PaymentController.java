package com.example.aura_mart.controller;

import com.example.aura_mart.dto.PaymentRequestDTO;
import com.example.aura_mart.dto.PaymentResponseDTO;
import com.example.aura_mart.service.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Controller", description = "Payment processing operations")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create-intent")
    @Operation(summary = "Create a Stripe PaymentIntent", description = "Generates a client secret for frontend payment")
    public ResponseEntity<String> createPaymentIntent(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount) throws StripeException {
        String clientSecret = paymentService.createPaymentIntent(userId, amount);
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                .body(clientSecret);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/complete")
    @Operation(summary = "Complete payment and create order", description = "Processes payment and creates an order after frontend payment")
    public ResponseEntity<PaymentResponseDTO> completePaymentAndCreateOrder(
            @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) throws StripeException {
        PaymentResponseDTO response = paymentService.completePaymentAndCreateOrder(paymentRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment details by payment ID")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long paymentId) {
        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by Order ID", description = "Retrieves payment details by order ID")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentResponseDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }
}