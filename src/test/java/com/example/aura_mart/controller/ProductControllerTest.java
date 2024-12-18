package com.example.aura_mart.controller;

import com.example.aura_mart.dto.ProductDTO;
import com.example.aura_mart.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        value = ProductController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;  // Mocked service bean

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;


    @BeforeEach
    void setUp() {
        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .stockQuantity(10)
                .size("M")
                .color("Red")
                .brand("Test Brand")
                .gender("Unisex")
                .categoryId(1L)
                .isActive(true)
                .build();
    }

    @Test
    void createProduct() throws Exception {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(100));
    }

    @Test
    void updateProduct() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void deleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getAllActiveProducts() throws Exception {
        List<ProductDTO> products = Collections.singletonList(productDTO);
        when(productService.getAllActiveProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getProductsByBrand() throws Exception {
        List<ProductDTO> products = Collections.singletonList(productDTO);
        when(productService.getProductsByBrand("Test Brand")).thenReturn(products);

        mockMvc.perform(get("/api/products/brand/{brand}", "Test Brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getProductsByGender() throws Exception {
        List<ProductDTO> products = Collections.singletonList(productDTO);
        when(productService.getProductsByGender("Unisex")).thenReturn(products);

        mockMvc.perform(get("/api/products/gender/{gender}", "Unisex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getProductsByPriceRange() throws Exception {
        List<ProductDTO> products = Collections.singletonList(productDTO);
        when(productService.getProductsByPriceRange(BigDecimal.valueOf(50), BigDecimal.valueOf(150)))
                .thenReturn(products);

        mockMvc.perform(get("/api/products/price-range")
                        .param("minPrice", "50")
                        .param("maxPrice", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchProducts() throws Exception {
        List<ProductDTO> products = Collections.singletonList(productDTO);
        when(productService.searchProducts(eq("Test Product"), eq("Test Brand"), eq(BigDecimal.valueOf(50)), eq(BigDecimal.valueOf(150))))
                .thenReturn(products);

        mockMvc.perform(get("/api/products/search")
                        .param("name", "Test Product")
                        .param("brand", "Test Brand")
                        .param("minPrice", "50")
                        .param("maxPrice", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getNewArrivals() throws Exception {
        List<ProductDTO> products = Collections.singletonList(productDTO);
        when(productService.getNewArrivals()).thenReturn(products);

        mockMvc.perform(get("/api/products/new-arrivals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getProductsByCategory() throws Exception {
        List<ProductDTO> products = Collections.singletonList(productDTO);
        when(productService.getProductsByCategory(1L)).thenReturn(products);

        mockMvc.perform(get("/api/products/category/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
