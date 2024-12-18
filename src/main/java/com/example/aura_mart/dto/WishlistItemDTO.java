package com.example.aura_mart.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Wishlist Item Data Transfer Object")
public class WishlistItemDTO {
    @Schema(description = "Unique identifier of the wishlist item", example = "1")
    private Long wishlistItemId;

    @NotNull(message = "Product ID is required")
    @Schema(description = "ID of the product in wishlist", example = "101")
    private Long productId;

    @Schema(description = "Name of the product", example = "Summer Dress")
    private String productName;

    @Schema(description = "Price of the product", example = "49.99")
    private BigDecimal productPrice;

    @Schema(description = "Time when item was added to wishlist")
    private LocalDateTime addedAt;
}