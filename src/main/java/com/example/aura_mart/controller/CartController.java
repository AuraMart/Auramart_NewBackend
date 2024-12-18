package com.example.aura_mart.controller;

import com.example.aura_mart.dto.CartDTO;
import com.example.aura_mart.dto.CartItemDTO;
import com.example.aura_mart.dto.CartRequest;
import com.example.aura_mart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "APIs for managing shopping cart")
public class CartController {
    private final CartService cartService;

    @PostMapping
    @Operation(summary = "Create a new cart", description = "Creates a cart for a user with initial items")
    @ApiResponse(responseCode = "200", description = "Cart created successfully")
    public ResponseEntity<CartDTO> createCart(
            @Valid @RequestBody CartRequest cartRequest
    ) {
        return ResponseEntity.ok(cartService.createCart(cartRequest));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get cart by user ID", description = "Retrieves a user's cart")
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully")
    public ResponseEntity<CartDTO> getCartByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/user/{userId}/items")
    @Operation(summary = "Add item to cart", description = "Adds a new item to the user's cart")
    @ApiResponse(responseCode = "200", description = "Item added to cart successfully")
    public ResponseEntity<CartDTO> addItemToCart(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody CartItemDTO cartItemDTO
    ) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, cartItemDTO));
    }

    @DeleteMapping("/user/{userId}/items/{productId}")
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the user's cart")
    @ApiResponse(responseCode = "200", description = "Item removed from cart successfully")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, productId));
    }

    @PutMapping("/user/{userId}/items/{productId}")
    @Operation(summary = "Update item quantity", description = "Updates the quantity of a specific item in the cart")
    @ApiResponse(responseCode = "200", description = "Item quantity updated successfully")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId,
            @Parameter(description = "New quantity", required = true)
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, productId, quantity));
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Clear cart", description = "Removes all items from the user's cart")
    @ApiResponse(responseCode = "200", description = "Cart cleared successfully")
    public ResponseEntity<Void> clearCart(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId
    ) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}