package com.example.aura_mart.controller;


import com.example.aura_mart.dto.ProductDTO;
import com.example.aura_mart.dto.ProductStatisticsDTO;
import com.example.aura_mart.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing fashion store products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create a new product", description = "Add a new product to the store")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product data")
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody ProductDTO productDTO
    ) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product", description = "Update details of a specific product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO
    ) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a product", description = "Mark a product as inactive")
    @ApiResponse(responseCode = "204", description = "Product marked as inactive")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductDTO> getProductById(
            @PathVariable Long id
    ) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active products", description = "Retrieve a list of all active products")
    public ResponseEntity<List<ProductDTO>> getAllActiveProducts() {
        List<ProductDTO> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "Get products by brand", description = "Retrieve products from a specific brand")
    public ResponseEntity<List<ProductDTO>> getProductsByBrand(
            @PathVariable String brand
    ) {
        List<ProductDTO> products = productService.getProductsByBrand(brand);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/gender/{gender}")
    @Operation(summary = "Get products by gender", description = "Retrieve products for a specific gender category")
    public ResponseEntity<List<ProductDTO>> getProductsByGender(
            @PathVariable String gender
    ) {
        List<ProductDTO> products = productService.getProductsByGender(gender);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/price-range")
    @Operation(summary = "Get products by price range", description = "Retrieve products within a specified price range")
    @ApiResponse(responseCode = "200", description = "Products found successfully")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice
    ) {
        List<ProductDTO> products = productService.getProductsByPriceRange(
                minPrice != null ? minPrice : BigDecimal.ZERO,
                maxPrice != null ? maxPrice : BigDecimal.valueOf(Long.MAX_VALUE)
        );
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(summary = "Advanced product search", description = "Search products with multiple optional filters")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @Parameter(description = "Product name (partial match)")
            @RequestParam(required = false) String name,

            @Parameter(description = "Brand name")
            @RequestParam(required = false) String brand,

            @Parameter(description = "Minimum price")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Maximum price")
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        List<ProductDTO> products = productService.searchProducts(name, brand, minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Get new arrival products",
            description = "Fetches products created during the current week that are active."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of new arrival products returned successfully"),
            @ApiResponse(responseCode = "500", description = "Server error occurred while fetching products")
    })
    @GetMapping("/new-arrivals")
    public List<ProductDTO> getNewArrivals() {
        return productService.getNewArrivals();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/statistics")
    public ResponseEntity<ProductStatisticsDTO> getProductStatistics() {
        return ResponseEntity.ok(productService.getProductStatistics());
    }

}