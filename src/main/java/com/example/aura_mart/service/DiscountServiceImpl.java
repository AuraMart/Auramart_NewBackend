package com.example.aura_mart.service;

import com.example.aura_mart.dto.DiscountRequestDTO;
import com.example.aura_mart.dto.DiscountResponseDTO;
import com.example.aura_mart.exception.ResourceNotFoundException;
import com.example.aura_mart.model.Discount;
import com.example.aura_mart.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;

    @Override
    @Transactional
    public DiscountResponseDTO createDiscount(DiscountRequestDTO discountRequestDTO) {
        // Check if discount code already exists
        if (discountRepository.existsByDiscountCode(discountRequestDTO.getDiscountCode())) {
            throw new IllegalArgumentException("Discount code already exists");
        }

        Discount discount = Discount.builder()
                .discountCode(discountRequestDTO.getDiscountCode())
                .discountPercentage(discountRequestDTO.getDiscountPercentage())
                .validFrom(discountRequestDTO.getValidFrom())
                .validTo(discountRequestDTO.getValidTo())
                .description(discountRequestDTO.getDescription())
                .imageUrl(discountRequestDTO.getImageUrl())
                .isActive(discountRequestDTO.isActive())
                .build();

        Discount savedDiscount = discountRepository.save(discount);
        return mapToResponseDTO(savedDiscount);
    }

    @Override
    @Transactional
    public DiscountResponseDTO updateDiscount(Long discountId, DiscountRequestDTO discountRequestDTO) {
        Discount existingDiscount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + discountId));

        existingDiscount.setDiscountCode(discountRequestDTO.getDiscountCode());
        existingDiscount.setDiscountPercentage(discountRequestDTO.getDiscountPercentage());
        existingDiscount.setValidFrom(discountRequestDTO.getValidFrom());
        existingDiscount.setValidTo(discountRequestDTO.getValidTo());
        existingDiscount.setDescription(discountRequestDTO.getDescription());
        existingDiscount.setActive(discountRequestDTO.isActive());

        Discount updatedDiscount = discountRepository.save(existingDiscount);
        return mapToResponseDTO(updatedDiscount);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponseDTO getDiscountById(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + discountId));
        return mapToResponseDTO(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponseDTO getDiscountByCode(String discountCode) {
        Discount discount = discountRepository.findByDiscountCode(discountCode)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with code: " + discountCode));
        return mapToResponseDTO(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponseDTO> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponseDTO> getActiveDiscounts() {
        return discountRepository.findActiveDiscounts(LocalDateTime.now()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDiscount(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + discountId));
        discountRepository.delete(discount);
    }

    // Helper method to convert Discount to DiscountResponseDTO
    private DiscountResponseDTO mapToResponseDTO(Discount discount) {
        return DiscountResponseDTO.builder()
                .discountId(discount.getDiscountId())
                .discountCode(discount.getDiscountCode())
                .discountPercentage(discount.getDiscountPercentage())
                .validFrom(discount.getValidFrom())
                .imageUrl(discount.getImageUrl())
                .validTo(discount.getValidTo())
                .description(discount.getDescription())
                .isActive(discount.isActive())
                .build();
    }
}

