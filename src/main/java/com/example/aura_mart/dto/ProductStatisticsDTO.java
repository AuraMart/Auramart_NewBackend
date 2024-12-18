package com.example.aura_mart.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductStatisticsDTO {
    private long totalProductCount;
    private long activeProductCount;
    private long inactiveProductCount;
    private BigDecimal totalInventoryValue;
    private BigDecimal averageProductPrice;
    private Map<String, Long> productCountByBrand;
    private Map<String, Long> productCountByGender;
    private BigDecimal minimumPrice;
    private BigDecimal maximumPrice;
}