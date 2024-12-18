package com.example.aura_mart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public   class CategoryProductCount {
    private String categoryName;
    private long productCount;
}
