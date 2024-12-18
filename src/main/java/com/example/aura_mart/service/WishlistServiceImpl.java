package com.example.aura_mart.service;

import com.example.aura_mart.dto.WishlistDTO;
import com.example.aura_mart.dto.WishlistItemDTO;
import com.example.aura_mart.exception.ConflictException;
import com.example.aura_mart.exception.ResourceNotFoundException;
import com.example.aura_mart.model.Product;
import com.example.aura_mart.model.User;
import com.example.aura_mart.model.Wishlist;
import com.example.aura_mart.model.WishlistItem;
import com.example.aura_mart.repository.ProductRepository;
import com.example.aura_mart.repository.UserRepository;
import com.example.aura_mart.repository.WishlistItemRepository;
import com.example.aura_mart.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public WishlistDTO getWishlistByUserId(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user"));

        List<WishlistItemDTO> wishlistItems = wishlist.getWishlistItems().stream()
                .map(this::convertToWishlistItemDTO)
                .collect(Collectors.toList());

        WishlistDTO wishlistDTO = new WishlistDTO();
        wishlistDTO.setWishlistId(wishlist.getWishlistId());
        wishlistDTO.setUserId(userId);
        wishlistDTO.setWishlistItems(wishlistItems);
        wishlistDTO.setTotalItems(wishlistItems.size());

        return wishlistDTO;
    }

    @Override
    public WishlistItemDTO addItemToWishlist(Long userId, Long productId) {
        // Validate user and product
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Get or create wishlist
        Wishlist wishlist = wishlistRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUser(user);
                    return wishlistRepository.save(newWishlist);
                });

        // Check if item already exists
        Optional<WishlistItem> existingItem = wishlistItemRepository
                .findByWishlist_WishlistIdAndProductId(wishlist.getWishlistId(), productId);

        if (existingItem.isPresent()) {
            throw new ConflictException("Product already in wishlist");
        }

        // Create new wishlist item
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setWishlist(wishlist);
        wishlistItem.setProduct(product);
        wishlistItem.setAddedAt(LocalDateTime.now());

        wishlistItemRepository.save(wishlistItem);

        return convertToWishlistItemDTO(wishlistItem);
    }

    @Override
    public void removeItemFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        WishlistItem wishlistItem = wishlistItemRepository
                .findByWishlist_WishlistIdAndProductId(wishlist.getWishlistId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in wishlist"));

        wishlistItemRepository.delete(wishlistItem);
    }

    @Override
    public void clearWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        wishlistItemRepository.deleteAll(wishlist.getWishlistItems());
    }

    private WishlistItemDTO convertToWishlistItemDTO(WishlistItem wishlistItem) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setWishlistItemId(wishlistItem.getWishlistItemId());
        dto.setProductId(wishlistItem.getProduct().getId());
        dto.setProductName(wishlistItem.getProduct().getName());
        dto.setProductPrice(wishlistItem.getProduct().getPrice());
        dto.setAddedAt(wishlistItem.getAddedAt());
        return dto;
    }
}