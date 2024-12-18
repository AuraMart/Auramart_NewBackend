package com.example.aura_mart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order Data Transfer Object")
public class OrderDTO {
    @Schema(description = "Unique identifier for the order")
    private Long orderId;

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user placing the order")
    private Long userId;

    @Schema(description = "Discount applied to the order")
    private Long discountId;

    @Positive(message = "Total amount must be positive")
    @Schema(description = "Total amount of the order")
    private BigDecimal totalAmount;

    @Schema(description = "Amount after applying discount")
    private BigDecimal discountedAmount;

    @Schema(description = "Current status of the order")
    private String status;

    @Schema(description = "Date and time of order placement")
    private LocalDateTime orderDate;

    @Schema(description = "List of items in the order")
    private List<OrderItemDTO> orderItems;
}