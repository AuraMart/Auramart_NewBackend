package com.example.aura_mart.service;

import com.example.aura_mart.dto.CategoryProductCount;
import com.example.aura_mart.dto.PaymentResponseDTO;
import com.example.aura_mart.dto.ProductDTO;
import com.example.aura_mart.dto.ProductStatisticsDTO;
import com.example.aura_mart.enums.GenderCategory;
import com.example.aura_mart.model.Category;
import com.example.aura_mart.model.Product;
import com.example.aura_mart.repository.CategoryRepository;
import com.example.aura_mart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        BeanUtils.copyProperties(productDTO, existingProduct, "id", "createdAt");

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existingProduct.setCategory(category);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return convertToDTO(product);
    }

    @Override
    public List<ProductDTO> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByGender(String gender) {
        return productRepository.findByGender(GenderCategory.valueOf(gender)).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProducts(String name, String brand, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.searchProducts(name, brand, minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product, "categoryId");

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        product.setGender(GenderCategory.valueOf(dto.getGender()));
        return product;
    }

    @Override
    public List<ProductDTO> getNewArrivals() {
        LocalDateTime startOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime endOfWeek = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return productRepository.findNewArrivals(startOfWeek, endOfWeek).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_Id(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }

        dto.setGender(product.getGender().name());
        return dto;
    }

    @Override
    public ProductStatisticsDTO getProductStatistics() {
        ProductStatisticsDTO statisticsDTO = new ProductStatisticsDTO();

        statisticsDTO.setTotalProductCount(productRepository.countTotalProducts());
        statisticsDTO.setActiveProductCount(productRepository.countActiveProducts());
        statisticsDTO.setInactiveProductCount(productRepository.countInactiveProducts());
        statisticsDTO.setTotalInventoryValue(productRepository.calculateTotalInventoryValue());
        statisticsDTO.setAverageProductPrice(productRepository.calculateAverageProductPrice());
        statisticsDTO.setMinimumPrice(productRepository.findMinimumPrice());
        statisticsDTO.setMaximumPrice(productRepository.findMaximumPrice());

        // Process brand count
        Map<String, Long> brandCountMap = productRepository.countProductsByBrand().stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
        statisticsDTO.setProductCountByBrand(brandCountMap);

        // Process gender count
        Map<String, Long> genderCountMap = productRepository.countProductsByGender().stream()
                .collect(Collectors.toMap(
                        arr -> ((GenderCategory) arr[0]).name(),
                        arr -> (Long) arr[1]
                ));
        statisticsDTO.setProductCountByGender(genderCountMap);

        return statisticsDTO;
    }

//    @Override
//    public List<CategoryProductCount> getProductCountByCategory() {
//        return productRepository.findProductCountByCategory();
//    }
}