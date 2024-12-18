package com.example.aura_mart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Discount Response DTO")
public class DiscountResponseDTO {
    @Schema(description = "Discount ID", example = "1")
    private Long discountId;

    @Schema(description = "Unique discount code", example = "SUMMER2024")
    private String discountCode;

    @Schema(description = "Discount percentage", example = "20.00")
    private BigDecimal discountPercentage;

    @Schema(description = "Discount start date", example = "2024-06-01T00:00:00")
    private LocalDateTime validFrom;

    @Schema(description = "Discount end date", example = "2024-08-31T23:59:59")
    private LocalDateTime validTo;

    @Schema(description = "Discount description", example = "Summer Sale Discount")
    private String description;

    @Schema(description = "Is discount currently active", example = "true")
    private boolean isActive;

    @Schema(description = "Image URL for the discount", example = "https://example.com/image.jpg")
    private String imageUrl;
}
