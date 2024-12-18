package com.example.aura_mart.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Cart Details")
public class CartDTO {
    @Schema(description = "Unique identifier of the cart", example = "1")
    private Long cartId;

    @Schema(description = "User ID associated with the cart", example = "456")
    private Long userId;

    @Schema(description = "List of cart items")
    private List<CartItemDTO> cartItems;

    @Schema(description = "Total amount of the cart", example = "99.98")
    private BigDecimal totalAmount;

    @Schema(description = "Timestamp when the cart was created")
    private LocalDateTime createdAt;
}
