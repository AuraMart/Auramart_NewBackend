package com.example.aura_mart.service;

import com.example.aura_mart.dto.CartDTO;
import com.example.aura_mart.dto.CartItemDTO;
import com.example.aura_mart.dto.CartRequest;
import com.example.aura_mart.model.Cart;
import com.example.aura_mart.model.CartItem;
import com.example.aura_mart.model.Product;
import com.example.aura_mart.model.User;
import com.example.aura_mart.repository.CartRepository;
import com.example.aura_mart.repository.ProductRepository;
import com.example.aura_mart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartDTO createCart(CartRequest cartRequest) {
        User user = userRepository.findById(cartRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setTotalAmount(BigDecimal.ZERO);

        List<CartItem> cartItems = cartRequest.getCartItems().stream()
                .map(itemDTO -> {
                    Product product = productRepository.findById(itemDTO.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    CartItem cartItem = new CartItem();
                    cartItem.setProduct(product);
                    cartItem.setQuantity(itemDTO.getQuantity());
                    cartItem.setCart(cart);
                    cartItem.setItemTotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

                    return cartItem;
                })
                .collect(Collectors.toList());

        cart.setCartItems(cartItems);
        cart.setTotalAmount(calculateTotalAmount(cartItems));

        Cart savedCart = cartRepository.save(cart);
        return mapCartToCartDTO(savedCart);
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));
        return mapCartToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long userId, CartItemDTO cartItemDTO) {
        // Find the user first
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Try to find an existing cart or create a new one if it doesn't exist
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    newCart.setCartItems(new ArrayList<>());
                    return newCart;
                });

        // Find the product
        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product already exists in cart
        CartItem existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingCartItem != null) {
            // Update existing cart item
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemDTO.getQuantity());
            existingCartItem.setItemTotal(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
        } else {
            // Create new cart item
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setQuantity(cartItemDTO.getQuantity());
            newCartItem.setCart(cart);
            newCartItem.setItemTotal(product.getPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())));
            cart.getCartItems().add(newCartItem);
        }

        // Recalculate total amount
        cart.setTotalAmount(calculateTotalAmount(cart.getCartItems()));

        // Save and return the cart
        Cart savedCart = cartRepository.save(cart);
        return mapCartToCartDTO(savedCart);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cart.setTotalAmount(calculateTotalAmount(cart.getCartItems()));

        Cart savedCart = cartRepository.save(cart);
        return mapCartToCartDTO(savedCart);
    }

    @Override
    @Transactional
    public CartDTO updateItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        Product product = cartItem.getProduct();
        cartItem.setQuantity(quantity);
        cartItem.setItemTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        cart.setTotalAmount(calculateTotalAmount(cart.getCartItems()));
        Cart savedCart = cartRepository.save(cart);
        return mapCartToCartDTO(savedCart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private BigDecimal calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItem::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Manual mapping method
    private CartDTO mapCartToCartDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setUserId(cart.getUser().getUserId());
        cartDTO.setCreatedAt(cart.getCreatedAt());
        cartDTO.setTotalAmount(cart.getTotalAmount());

        // Map cart items
        cartDTO.setCartItems(cart.getCartItems().stream()
                .map(this::mapCartItemToCartItemDTO)
                .collect(Collectors.toList()));

        return cartDTO;
    }

    // Manual mapping method for cart items
    private CartItemDTO mapCartItemToCartItemDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setCartItemId(cartItem.getCartItemId());
        cartItemDTO.setProductId(cartItem.getProduct().getId());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        cartItemDTO.setItemTotal(cartItem.getItemTotal());

        return cartItemDTO;
    }
}