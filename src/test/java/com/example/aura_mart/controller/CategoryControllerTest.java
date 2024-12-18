package com.example.aura_mart.controller;

import com.example.aura_mart.dto.CategoryRequestDTO;
import com.example.aura_mart.dto.CategoryResponseDTO;
import com.example.aura_mart.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = CategoryController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryRequestDTO categoryRequestDTO;
    private CategoryResponseDTO categoryResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup sample DTOs for testing
        categoryRequestDTO = new CategoryRequestDTO(
                "Test Category",
                "A test category description",
                null,
                "http://test-image-url.com"
        );

        categoryResponseDTO = new CategoryResponseDTO(
                1L,
                "Test Category",
                "A test category description",
                null,
                "http://test-image-url.com"
        );
    }

    @Test
    void createCategory() throws Exception {
        when(categoryService.createCategory(any(CategoryRequestDTO.class)))
                .thenReturn(categoryResponseDTO);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void updateCategory() throws Exception {
        when(categoryService.updateCategory(eq(1L), any(CategoryRequestDTO.class)))
                .thenReturn(categoryResponseDTO);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void deleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllCategories() throws Exception {
        List<CategoryResponseDTO> categories = Arrays.asList(
                categoryResponseDTO,
                new CategoryResponseDTO(
                        2L,
                        "Another Category",
                        "Another description",
                        null,
                        "http://another-image-url.com"
                )
        );

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test Category"));
    }

    @Test
    void getRootCategories() throws Exception {
        List<CategoryResponseDTO> rootCategories = Arrays.asList(
                categoryResponseDTO
        );

        when(categoryService.getRootCategories()).thenReturn(rootCategories);

        mockMvc.perform(get("/api/categories/roots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Category"));
    }

    @Test
    void getSubCategories() throws Exception {
        List<CategoryResponseDTO> subCategories = Arrays.asList(
                new CategoryResponseDTO(
                        2L,
                        "Subcategory",
                        "A subcategory description",
                        1L,
                        "http://subcategory-image-url.com"
                )
        );

        when(categoryService.getSubCategories(1L)).thenReturn(subCategories);

        mockMvc.perform(get("/api/categories/subcategories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].parentCategoryId").value(1L));
    }

    @Test
    void getCategoryById() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(categoryResponseDTO);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Category"));
    }
}