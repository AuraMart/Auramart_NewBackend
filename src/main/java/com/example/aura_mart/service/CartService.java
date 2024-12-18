package com.example.aura_mart.service;

import com.example.aura_mart.dto.CartDTO;
import com.example.aura_mart.dto.CartItemDTO;
import com.example.aura_mart.dto.CartRequest;

public interface CartService {
    CartDTO createCart(CartRequest cartRequest);
    CartDTO getCartByUserId(Long userId);
    CartDTO addItemToCart(Long userId, CartItemDTO cartItemDTO);
    CartDTO removeItemFromCart(Long userId, Long productId);
    CartDTO updateItemQuantity(Long userId, Long productId, int quantity);
    void clearCart(Long userId);
}