package com.example.aura_mart.dto;

import com.example.aura_mart.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment Response DTO")
public class PaymentResponseDTO {
    @Schema(description = "Payment ID")
    private Long paymentId;

    @Schema(description = "Order ID")
    private Long orderId;

    @Schema(description = "User ID")
    private Long userId;

    @Schema(description = "Payment Method")
    private String paymentMethod;

    @Schema(description = "Payment Status")
    private PaymentStatus paymentStatus;

    @Schema(description = "Payment Amount")
    private BigDecimal amount;

    @Schema(description = "Payment Date")
    private LocalDateTime paymentDate;

    @Schema(description = "Stripe Transaction ID")
    private String stripeTransactionId;
}