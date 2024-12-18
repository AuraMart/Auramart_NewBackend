package com.example.aura_mart.repository;

import com.example.aura_mart.dto.ProductStatisticsDTO;
import com.example.aura_mart.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentCategoryIsNull();
    List<Category> findByParentCategory_Id(Long parentCategoryId);
}