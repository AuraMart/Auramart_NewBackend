package com.example.aura_mart.repository;


import com.example.aura_mart.dto.ProductDTO;
import com.example.aura_mart.enums.GenderCategory;
import com.example.aura_mart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrue();
    List<Product> findByBrand(String brand);
    List<Product> findByGender(GenderCategory gender);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByCategory_Id(Long categoryId);

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +
            "(:brand IS NULL OR p.brand = :brand) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> searchProducts(
            @Param("name") String name,
            @Param("brand") String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT p FROM Product p WHERE p.createdAt >= :startOfWeek AND p.createdAt <= :endOfWeek AND p.isActive = true")
    List<Product> findNewArrivals(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek);

    long countByIsActiveTrue();
    long countByGender(GenderCategory gender);

    @Query("SELECT MIN(p.price) FROM Product p")
    BigDecimal findMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    BigDecimal findMaxPrice();

    @Query("SELECT AVG(p.price) FROM Product p")
    BigDecimal findAveragePrice();

    @Query("SELECT COUNT(p) FROM Product p")
    long countTotalProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    long countActiveProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = false")
    long countInactiveProducts();

    @Query("SELECT SUM(p.price * p.stockQuantity) FROM Product p")
    BigDecimal calculateTotalInventoryValue();

    @Query("SELECT AVG(p.price) FROM Product p")
    BigDecimal calculateAverageProductPrice();

    @Query("SELECT p.brand, COUNT(p) FROM Product p GROUP BY p.brand")
    List<Object[]> countProductsByBrand();

    @Query("SELECT p.gender, COUNT(p) FROM Product p GROUP BY p.gender")
    List<Object[]> countProductsByGender();

    @Query("SELECT MIN(p.price) FROM Product p")
    BigDecimal findMinimumPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    BigDecimal findMaximumPrice();
}
