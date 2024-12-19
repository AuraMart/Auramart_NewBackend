package com.example.aura_mart.controller;

        import com.example.aura_mart.dto.WishlistDTO;
        import com.example.aura_mart.dto.WishlistItemDTO;
        import com.example.aura_mart.service.WishlistService;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import org.junit.jupiter.api.extension.ExtendWith;
        import org.mockito.InjectMocks;
        import org.mockito.Mock;
        import org.mockito.junit.jupiter.MockitoExtension;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;

        import java.math.BigDecimal;
        import java.time.LocalDateTime;
        import java.util.ArrayList;
        import java.util.List;

        import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    private WishlistDTO mockWishlistDTO;
    private WishlistItemDTO mockWishlistItemDTO;
    private final Long USER_ID = 1L;
    private final Long PRODUCT_ID = 101L;

    @BeforeEach
    void setUp() {
        // Initialize mock WishlistItemDTO
        mockWishlistItemDTO = new WishlistItemDTO(
                1L,
                PRODUCT_ID,
                "Test Product",
                new BigDecimal("99.99"),
                LocalDateTime.now()
        );

        // Initialize mock WishlistDTO
        List<WishlistItemDTO> items = new ArrayList<>();
        items.add(mockWishlistItemDTO);
        mockWishlistDTO = new WishlistDTO(1L, USER_ID, items, 1);
    }

    @Test
    void getWishlist_Success() {
        // Arrange
        when(wishlistService.getWishlistByUserId(USER_ID)).thenReturn(mockWishlistDTO);

        // Act
        ResponseEntity<WishlistDTO> response = wishlistController.getWishlist(USER_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockWishlistDTO, response.getBody());
        verify(wishlistService, times(1)).getWishlistByUserId(USER_ID);
    }

    @Test
    void addToWishlist_Success() {
        // Arrange
        when(wishlistService.addItemToWishlist(USER_ID, PRODUCT_ID)).thenReturn(mockWishlistItemDTO);

        // Act
        ResponseEntity<WishlistItemDTO> response = wishlistController.addToWishlist(USER_ID, PRODUCT_ID);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockWishlistItemDTO, response.getBody());
        verify(wishlistService, times(1)).addItemToWishlist(USER_ID, PRODUCT_ID);
    }

    @Test
    void removeFromWishlist_Success() {
        // Arrange
        doNothing().when(wishlistService).removeItemFromWishlist(USER_ID, PRODUCT_ID);

        // Act
        ResponseEntity<Void> response = wishlistController.removeFromWishlist(USER_ID, PRODUCT_ID);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(wishlistService, times(1)).removeItemFromWishlist(USER_ID, PRODUCT_ID);
    }

    @Test
    void clearWishlist_Success() {
        // Arrange
        doNothing().when(wishlistService).clearWishlist(USER_ID);

        // Act
        ResponseEntity<Void> response = wishlistController.clearWishlist(USER_ID);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(wishlistService, times(1)).clearWishlist(USER_ID);
    }

    // Additional test cases for error scenarios
    @Test
    void getWishlist_WhenServiceThrowsException() {
        // Arrange
        when(wishlistService.getWishlistByUserId(USER_ID))
                .thenThrow(new RuntimeException("Wishlist not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> wishlistController.getWishlist(USER_ID));
        verify(wishlistService, times(1)).getWishlistByUserId(USER_ID);
    }

    @Test
    void addToWishlist_WhenServiceThrowsException() {
        // Arrange
        when(wishlistService.addItemToWishlist(USER_ID, PRODUCT_ID))
                .thenThrow(new RuntimeException("Product already in wishlist"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> wishlistController.addToWishlist(USER_ID, PRODUCT_ID));
        verify(wishlistService, times(1)).addItemToWishlist(USER_ID, PRODUCT_ID);
    }
}
