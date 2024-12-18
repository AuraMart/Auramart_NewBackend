package com.example.aura_mart.service;

import com.example.aura_mart.dto.OrderDTO;
import com.example.aura_mart.dto.OrderItemDTO;
import com.example.aura_mart.enums.OrderStatus;
import com.example.aura_mart.model.Discount;
import com.example.aura_mart.model.Order;
import com.example.aura_mart.model.OrderItem;
import com.example.aura_mart.model.Product;
import com.example.aura_mart.model.User;
import com.example.aura_mart.repository.DiscountRepository;
import com.example.aura_mart.repository.OrderRepository;
import com.example.aura_mart.repository.ProductRepository;
import com.example.aura_mart.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // Validate user
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create Order entity
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // Set order items with product validation
        List<OrderItem> orderItems = orderDTO.getOrderItems().stream()
                .map(itemDTO -> {
                    // Find and validate product
                    Product product = productRepository.findById(itemDTO.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    // Check stock availability
                    if (product.getStockQuantity() < itemDTO.getQuantity()) {
                        throw new RuntimeException("Insufficient stock for product: " + product.getName());
                    }

                    // Create OrderItem
                    OrderItem item = new OrderItem();
                    item.setQuantity(itemDTO.getQuantity());
                    item.setItemPrice(product.getPrice());
                    item.setProduct(product);
                    item.setOrder(order);

                    // Reduce product stock
                    product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
                    productRepository.save(product);

                    return item;
                })
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);

        // Calculate total amount
        BigDecimal totalAmount = calculateOrderTotal(orderDTO);
        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Convert and return DTO
        return convertToDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }

    @Override
    public List<OrderDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUser_UserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus newStatus = OrderStatus.valueOf(status);
        order.setStatus(newStatus);

        // Additional logic for specific status changes
        if (newStatus == OrderStatus.CANCELLED) {
            // Restore product stock when order is cancelled
            order.getOrderItems().forEach(item -> {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            });
        }

        return convertToDTO(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDTO applyDiscount(Long orderId, Long discountId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        // Check discount validity
        LocalDateTime now = LocalDateTime.now();
        if (discount.getValidFrom().isAfter(now) || discount.getValidTo().isBefore(now)) {
            throw new RuntimeException("Discount is not currently valid");
        }

        order.setDiscount(discount);
        order.setDiscountedAmount(calculateDiscountedAmount(order, discount));

        return convertToDTO(orderRepository.save(order));
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Restore product stock
        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return true;
    }

    @Override
    public BigDecimal calculateOrderTotal(OrderDTO orderDTO) {
        return orderDTO.getOrderItems().stream()
                .map(item -> {
                    // Fetch product price to ensure accurate pricing
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Convert Order entity to OrderDTO
    private OrderDTO convertToDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUser().getUserId())
                .totalAmount(order.getTotalAmount())
                .discountedAmount(order.getDiscountedAmount())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .orderItems(order.getOrderItems().stream()
                        .map(item -> OrderItemDTO.builder()
                                .orderItemId(item.getOrderItemId())
                                .productId(item.getProduct().getId())
                                .quantity(item.getQuantity())
                                .itemPrice(item.getItemPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Calculate discounted amount
    private BigDecimal calculateDiscountedAmount(Order order, Discount discount) {
        BigDecimal totalAmount = order.getTotalAmount();
        BigDecimal discountPercentage = discount.getDiscountPercentage()
                .divide(BigDecimal.valueOf(100));
        return totalAmount.subtract(totalAmount.multiply(discountPercentage));
    }
}