package com.example.aura_mart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Discount Creation Request DTO")
public class DiscountRequestDTO {
    @Schema(description = "Unique discount code", example = "SUMMER2024")
    @NotBlank(message = "Discount code cannot be blank")
    @Size(min = 3, max = 20, message = "Discount code must be between 3 and 20 characters")
    private String discountCode;

    @Schema(description = "Discount percentage", example = "20.00")
    @NotNull(message = "Discount percentage cannot be null")
    @DecimalMin(value = "0.00", message = "Discount percentage must be positive")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100%")
    private BigDecimal discountPercentage;

    @Schema(description = "Discount start date", example = "2024-06-01T00:00:00")
    @NotNull(message = "Valid from date cannot be null")
    private LocalDateTime validFrom;

    @Schema(description = "Discount end date", example = "2024-08-31T23:59:59")
    @NotNull(message = "Valid to date cannot be null")
    private LocalDateTime validTo;

    @Schema(description = "Discount description", example = "Summer Sale Discount")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Schema(description = "Is discount currently active", example = "true")
    private boolean isActive;

    @Schema(description = "Image URL for the discount", example = "https://example.com/image.jpg")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;
}
