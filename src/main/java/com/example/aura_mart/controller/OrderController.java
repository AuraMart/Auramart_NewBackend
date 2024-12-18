package com.example.aura_mart.controller;

import com.example.aura_mart.dto.OrderDTO;
import com.example.aura_mart.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order for a user")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve order details by order ID")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        OrderDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user", description = "Retrieve all orders for a specific user")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Update the status of an existing order")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/discount/{discountId}")
    @Operation(summary = "Apply discount to order", description = "Apply a specific discount to an existing order")
    public ResponseEntity<OrderDTO> applyDiscount(
            @PathVariable Long orderId,
            @PathVariable Long discountId) {
        OrderDTO orderWithDiscount = orderService.applyDiscount(orderId, discountId);
        return ResponseEntity.ok(orderWithDiscount);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order", description = "Cancel an existing order and restore product stock")
    public ResponseEntity<Boolean> cancelOrder(@PathVariable Long orderId) {
        boolean isCancelled = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(isCancelled);
    }
}