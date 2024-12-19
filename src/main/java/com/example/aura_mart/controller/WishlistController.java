package com.example.aura_mart.controller;

import com.example.aura_mart.dto.WishlistDTO;
import com.example.aura_mart.dto.WishlistItemDTO;
import com.example.aura_mart.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist Management", description = "Operations related to user wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user's wishlist", description = "Retrieve all items in the user's wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wishlist retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist not found")
    })
    public ResponseEntity<WishlistDTO> getWishlist(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlistByUserId(userId));
    }

    @PostMapping("/{userId}/add/{productId}")
    @Operation(summary = "Add item to wishlist", description = "Add a product to user's wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item added to wishlist"),
            @ApiResponse(responseCode = "400", description = "Invalid product"),
            @ApiResponse(responseCode = "409", description = "Product already in wishlist")
    })
    public ResponseEntity<WishlistItemDTO> addToWishlist(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        WishlistItemDTO addedItem = wishlistService.addItemToWishlist(userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItem);
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    @Operation(summary = "Remove item from wishlist", description = "Remove a specific product from user's wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found in wishlist")
    })
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        wishlistService.removeItemFromWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/clear")
    @Operation(summary = "Clear entire wishlist", description = "Remove all items from user's wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Wishlist cleared successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist not found")
    })
    public ResponseEntity<Void> clearWishlist(@PathVariable Long userId) {
        wishlistService.clearWishlist(userId);
        return ResponseEntity.noContent().build();
    }
}