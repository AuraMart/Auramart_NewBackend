package com.example.aura_mart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment Request Data Transfer Object")
public class PaymentRequestDTO {

    @Schema(description = "Unique identifier of the user making the payment", required = true)
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(description = "Payment Intent ID from Stripe")
    private String paymentIntentId;

    @Schema(description = "Method of payment used for the transaction")
    private String paymentMethod;

    @Schema(description = "Total amount to be paid for the order", required = true)
    @NotNull(message = "Payment amount is required")
    private BigDecimal amount;

    @Schema(description = "List of items included in the order", required = true)
    @NotNull(message = "Order items are required")
    private List<OrderItemDTO> orderItems;
}