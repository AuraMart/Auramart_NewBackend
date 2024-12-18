package com.example.aura_mart.service;

import com.example.aura_mart.dto.DiscountRequestDTO;
import com.example.aura_mart.dto.DiscountResponseDTO;

import java.util.List;

public interface DiscountService {
    DiscountResponseDTO createDiscount(DiscountRequestDTO discountRequestDTO);
    DiscountResponseDTO updateDiscount(Long discountId, DiscountRequestDTO discountRequestDTO);
    DiscountResponseDTO getDiscountById(Long discountId);
    DiscountResponseDTO getDiscountByCode(String discountCode);
    List<DiscountResponseDTO> getAllDiscounts();
    List<DiscountResponseDTO> getActiveDiscounts();
    void deleteDiscount(Long discountId);
}
