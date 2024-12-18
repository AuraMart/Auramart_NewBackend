
// CartRequest.java
package com.example.aura_mart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to add items to cart")
public class CartRequest {
    @Schema(description = "User ID", example = "456")
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(description = "List of cart items to add")
    @Valid
    private List<CartItemDTO> cartItems;
}