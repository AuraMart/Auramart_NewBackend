package com.example.aura_mart.service;

import com.example.aura_mart.dto.WishlistDTO;
import com.example.aura_mart.dto.WishlistItemDTO;

public interface WishlistService {
    WishlistDTO getWishlistByUserId(Long userId);
    WishlistItemDTO addItemToWishlist(Long userId, Long productId);
    void removeItemFromWishlist(Long userId, Long productId);
    void clearWishlist(Long userId);
}
