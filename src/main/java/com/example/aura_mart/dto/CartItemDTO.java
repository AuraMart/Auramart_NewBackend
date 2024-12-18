package com.example.aura_mart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Cart Item Details")
public class CartItemDTO {
    @Schema(description = "Unique identifier of the cart item", example = "1")
    private Long cartItemId;

    @Schema(description = "Product ID", example = "123")
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Schema(description = "Quantity of the product", example = "2")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Schema(description = "Total price for this cart item", example = "49.99")
    private BigDecimal itemTotal;
}
