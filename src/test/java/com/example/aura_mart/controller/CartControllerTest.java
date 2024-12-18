package com.example.aura_mart.controller;

import com.example.aura_mart.dto.CartDTO;
import com.example.aura_mart.dto.CartItemDTO;
import com.example.aura_mart.dto.CartRequest;
import com.example.aura_mart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = CartController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private CartDTO cartDTO;
    private CartItemDTO cartItemDTO;

    @BeforeEach
    void setUp() {
        // Create a sample CartItemDTO
        cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(1L);
        cartItemDTO.setQuantity(2);
        cartItemDTO.setItemTotal(BigDecimal.valueOf(99.98));

        // Create a sample CartDTO
        cartDTO = new CartDTO();
        cartDTO.setCartId(1L);
        cartDTO.setUserId(456L);
        cartDTO.setCartItems(Collections.singletonList(cartItemDTO));
        cartDTO.setTotalAmount(BigDecimal.valueOf(99.98));
        cartDTO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createCart() throws Exception {
        CartRequest cartRequest = new CartRequest();
        cartRequest.setUserId(456L);
        cartRequest.setCartItems(Collections.singletonList(cartItemDTO));

        when(cartService.createCart(any(CartRequest.class))).thenReturn(cartDTO);

        mockMvc.perform(post("/api/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L))
                .andExpect(jsonPath("$.userId").value(456L));
    }

    @Test
    void getCartByUserId() throws Exception {
        when(cartService.getCartByUserId(456L)).thenReturn(cartDTO);

        mockMvc.perform(get("/api/carts/user/{userId}", 456L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L))
                .andExpect(jsonPath("$.userId").value(456L));
    }

    @Test
    void addItemToCart() throws Exception {
        when(cartService.addItemToCart(eq(456L), any(CartItemDTO.class))).thenReturn(cartDTO);

        mockMvc.perform(post("/api/carts/user/{userId}/items", 456L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems[0].productId").value(1L));
    }

    @Test
    void removeItemFromCart() throws Exception {
        when(cartService.removeItemFromCart(456L, 1L)).thenReturn(cartDTO);

        mockMvc.perform(delete("/api/carts/user/{userId}/items/{productId}", 456L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L));
    }

    @Test
    void updateItemQuantity() throws Exception {
        when(cartService.updateItemQuantity(456L, 1L, 3)).thenReturn(cartDTO);

        mockMvc.perform(put("/api/carts/user/{userId}/items/{productId}", 456L, 1L)
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L));
    }

    @Test
    void clearCart() throws Exception {
        doNothing().when(cartService).clearCart(456L);

        mockMvc.perform(delete("/api/carts/user/{userId}", 456L))
                .andExpect(status().isOk());
    }

    // Validation Tests
    @Test
    void createCart_invalidRequest() throws Exception {
        CartItemDTO invalidCartItem = new CartItemDTO();
        invalidCartItem.setProductId(null);
        invalidCartItem.setQuantity(0);

        CartRequest invalidCartRequest = new CartRequest();
        invalidCartRequest.setUserId(456L);
        invalidCartRequest.setCartItems(Collections.singletonList(invalidCartItem));

        mockMvc.perform(post("/api/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCartRequest)))
                .andExpect(status().isBadRequest());
    }
}