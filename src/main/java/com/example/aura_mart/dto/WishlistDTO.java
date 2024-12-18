package com.example.aura_mart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Wishlist Data Transfer Object")
public class WishlistDTO {
    @Schema(description = "Unique identifier of the wishlist", example = "1")
    private Long wishlistId;

    @Schema(description = "User ID associated with the wishlist", example = "50")
    private Long userId;

    @Schema(description = "List of wishlist items")
    private List<WishlistItemDTO> wishlistItems;

    @Schema(description = "Total number of items in wishlist")
    private int totalItems;
}