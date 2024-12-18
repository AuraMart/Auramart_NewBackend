package com.example.aura_mart.repository;

import com.example.aura_mart.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByWishlist_WishlistId(Long wishlistId);
    Optional<WishlistItem> findByWishlist_WishlistIdAndProductId(Long wishlistId, Long productId);
}
