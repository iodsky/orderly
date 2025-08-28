package com.iodsky.orderly.service.category;

import java.util.List;

import com.iodsky.orderly.dto.category.CategoryDto;
import com.iodsky.orderly.model.Category;

public interface ICategoryService {
  CategoryDto addCategory(CategoryDto CategoryDto);

  Category getCategoryById(Long id);

  List<CategoryDto> getAllCategories();

  CategoryDto updateCategory(Long id, CategoryDto CategoryDto);

  void deleteCategoryById(Long id);
}
