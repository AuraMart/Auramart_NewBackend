package com.example.aura_mart.controller;

import com.example.aura_mart.dto.CategoryRequestDTO;
import com.example.aura_mart.dto.CategoryResponseDTO;
import com.example.aura_mart.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Controller", description = "APIs for managing categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Create a new category")
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.createCategory(categoryRequestDTO));
    }

    @Operation(summary = "Update an existing category")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryRequestDTO));
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Get root categories")
    @GetMapping("/roots")
    public ResponseEntity<List<CategoryResponseDTO>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }

    @Operation(summary = "Get subcategories by parent category ID")
    @GetMapping("/subcategories/{parentCategoryId}")
    public ResponseEntity<List<CategoryResponseDTO>> getSubCategories(@PathVariable Long parentCategoryId) {
        return ResponseEntity.ok(categoryService.getSubCategories(parentCategoryId));
    }

    @GetMapping("/{categoryId}")
    @Operation(
            summary = "Get Category by ID",
            description = "Retrieve a specific category by its unique identifier"
    )
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @Parameter(description = "Unique identifier of the category", required = true, example = "1")
            @PathVariable Long categoryId
    ) {
        CategoryResponseDTO category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }
}
