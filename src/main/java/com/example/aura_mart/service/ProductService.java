package com.example.aura_mart.service;

import com.example.aura_mart.dto.CategoryProductCount;
import com.example.aura_mart.dto.ProductDTO;
import com.example.aura_mart.dto.ProductStatisticsDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    ProductDTO getProductById(Long id);
    List<ProductDTO> getAllActiveProducts();
    List<ProductDTO> getProductsByBrand(String brand);
    List<ProductDTO> getProductsByGender(String gender);
    List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductDTO> searchProducts(String name, String brand, BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductDTO> getNewArrivals();
    List<ProductDTO> getProductsByCategory(Long categoryId);
    ProductStatisticsDTO getProductStatistics();
//    List<CategoryProductCount> getProductCountByCategory();
}
