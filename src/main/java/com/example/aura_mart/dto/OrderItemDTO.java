package com.example.aura_mart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order Item Data Transfer Object")
public class OrderItemDTO {
    @Schema(description = "Unique identifier for the order item")
    private Long orderItemId;

    @NotNull(message = "Product ID is required")
    @Schema(description = "ID of the product")
    private Long productId;

    @Positive(message = "Quantity must be positive")
    @Schema(description = "Quantity of the product")
    private int quantity;

    @Schema(description = "Price of the item")
    private BigDecimal itemPrice;
}