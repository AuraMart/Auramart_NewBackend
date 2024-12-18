package com.example.aura_mart.repository;

import com.example.aura_mart.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder_OrderId(Long orderId);
    Optional<Payment> findByStripeTransactionId(String transactionId);
    List<Payment> findAllByOrderByPaymentDateDesc();
}