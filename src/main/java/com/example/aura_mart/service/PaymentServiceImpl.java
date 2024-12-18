package com.example.aura_mart.service;

import com.example.aura_mart.dto.OrderDTO;
import com.example.aura_mart.dto.PaymentRequestDTO;
import com.example.aura_mart.dto.PaymentResponseDTO;
import com.example.aura_mart.enums.OrderStatus;
import com.example.aura_mart.enums.PaymentStatus;
import com.example.aura_mart.model.Order;
import com.example.aura_mart.model.Payment;
import com.example.aura_mart.model.User;
import com.example.aura_mart.repository.OrderRepository;
import com.example.aura_mart.repository.PaymentRepository;
import com.example.aura_mart.repository.ProductRepository;
import com.example.aura_mart.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final ProductRepository productRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    @Transactional
    public String createPaymentIntent(Long userId, BigDecimal amount) throws StripeException {
        // Set Stripe API Key
        Stripe.apiKey = stripeSecretKey;

        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create PaymentIntent in Stripe
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                .setCurrency("usd")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Return the client secret to be used by frontend
        return paymentIntent.getClientSecret();
    }

    @Override
    @Transactional
    public PaymentResponseDTO completePaymentAndCreateOrder(PaymentRequestDTO paymentRequestDTO) throws StripeException {
        // Set Stripe API Key
        Stripe.apiKey = stripeSecretKey;

        // Find user
        User user = userRepository.findById(paymentRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentRequestDTO.getPaymentIntentId());

        // Confirm the payment intent is successful
        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new RuntimeException("Payment not completed successfully");
        }

        // Prepare order DTO
        OrderDTO orderDTO = OrderDTO.builder()
                .userId(user.getUserId())
                .orderItems(paymentRequestDTO.getOrderItems())
                .status(OrderStatus.PENDING.name())
                .build();

        // Calculate total amount
        BigDecimal totalAmount = orderService.calculateOrderTotal(orderDTO);
        orderDTO.setTotalAmount(totalAmount);

        try {
            // Create order
            OrderDTO createdOrderDto = orderService.createOrder(orderDTO);

            Order order = orderRepository.findById(createdOrderDto.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Created order not found"));

            // Create payment record
            Payment payment = Payment.builder()
                    .order(order)
                    .paymentMethod(paymentRequestDTO.getPaymentMethod())
                    .paymentStatus(PaymentStatus.COMPLETED)
                    .amount(totalAmount)
                    .paymentDate(LocalDateTime.now())
                    .stripeTransactionId(paymentRequestDTO.getPaymentIntentId())
                    .build();

            payment = paymentRepository.save(payment);

            // Update order status
            order.setStatus(OrderStatus.CONFIRMED);
            order.setPayment(payment);
            orderRepository.save(order);

            // Prepare and return payment response
            return PaymentResponseDTO.builder()
                    .paymentId(payment.getPaymentId())
                    .orderId(order.getOrderId())
                    .userId(user.getUserId())
                    .paymentMethod(payment.getPaymentMethod())
                    .paymentStatus(payment.getPaymentStatus())
                    .amount(payment.getAmount())
                    .paymentDate(payment.getPaymentDate())
                    .stripeTransactionId(payment.getStripeTransactionId())
                    .build();

        } catch (Exception e) {
            // Handle payment and order creation failures
            throw new RuntimeException("Failed to process payment and create order", e);
        }
    }
    @Transactional(readOnly = true)
    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc().stream()
                .map(payment -> PaymentResponseDTO.builder()
                        .paymentId(payment.getPaymentId())
                        .orderId(payment.getOrder().getOrderId())
                        .userId(payment.getOrder().getUser().getUserId())
                        .paymentMethod(payment.getPaymentMethod())
                        .paymentStatus(payment.getPaymentStatus())
                        .amount(payment.getAmount())
                        .paymentDate(payment.getPaymentDate())
                        .stripeTransactionId(payment.getStripeTransactionId())
                        .build()
                ).collect(Collectors.toList());
    }



    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return PaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrder().getOrderId())
                .userId(payment.getOrder().getUser().getUserId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .stripeTransactionId(payment.getStripeTransactionId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for the given order"));

        return PaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrder().getOrderId())
                .userId(payment.getOrder().getUser().getUserId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .stripeTransactionId(payment.getStripeTransactionId())
                .build();
    }
}