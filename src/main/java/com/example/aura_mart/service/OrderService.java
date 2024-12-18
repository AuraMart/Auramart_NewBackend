package com.example.aura_mart.service;

import com.example.aura_mart.dto.OrderDTO;
import com.example.aura_mart.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getOrdersByUser(Long userId);
    OrderDTO updateOrderStatus(Long orderId, String status);
    OrderDTO applyDiscount(Long orderId, Long discountId);
    boolean cancelOrder(Long orderId);
    BigDecimal calculateOrderTotal(OrderDTO orderDTO);
    List<OrderDTO> getAllOrders();
}