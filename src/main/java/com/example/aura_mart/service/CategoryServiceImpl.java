package com.example.aura_mart.service;

import com.example.aura_mart.dto.CategoryRequestDTO;
import com.example.aura_mart.dto.CategoryResponseDTO;
import com.example.aura_mart.exception.CategoryNotFoundException;
import com.example.aura_mart.model.Category;
import com.example.aura_mart.repository.CategoryRepository;
import com.example.aura_mart.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        Category category = new Category();
        category.setName(categoryRequestDTO.getName());
        category.setDescription(categoryRequestDTO.getDescription());
        category.setImageUrl(categoryRequestDTO.getImageUrl());

        if (categoryRequestDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequestDTO.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDTO(savedCategory);
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(categoryRequestDTO.getName());
        category.setDescription(categoryRequestDTO.getDescription());

        if (categoryRequestDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequestDTO.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getSubCategories(Long parentCategoryId) {
        return categoryRepository.findByParentCategory_Id(parentCategoryId).stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
        return mapToResponseDTO(category);
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParentCategory() != null ? category.getParentCategory().getId() : null,
                category.getImageUrl()
        );
    }
}


