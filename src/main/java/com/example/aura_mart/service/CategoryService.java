package com.example.aura_mart.service;

import com.example.aura_mart.dto.CategoryRequestDTO;
import com.example.aura_mart.dto.CategoryResponseDTO;
import java.util.List;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO);
    void deleteCategory(Long id);
    List<CategoryResponseDTO> getAllCategories();
    List<CategoryResponseDTO> getRootCategories();
    List<CategoryResponseDTO> getSubCategories(Long parentCategoryId);
    CategoryResponseDTO getCategoryById(Long categoryId);
}
