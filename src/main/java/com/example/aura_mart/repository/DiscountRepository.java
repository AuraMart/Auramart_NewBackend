package com.example.aura_mart.repository;


import com.example.aura_mart.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findByDiscountCode(String discountCode);

    @Query("SELECT d FROM Discount d WHERE d.isActive = true AND d.validFrom <= :now AND d.validTo >= :now")
    List<Discount> findActiveDiscounts(LocalDateTime now);

    boolean existsByDiscountCode(String discountCode);
}