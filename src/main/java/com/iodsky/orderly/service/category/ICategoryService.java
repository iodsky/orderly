package com.iodsky.orderly.service.category;

import java.util.List;
import java.util.UUID;

import com.iodsky.orderly.dto.category.CategoryDto;
import com.iodsky.orderly.model.Category;

public interface ICategoryService {
  Category addCategory(CategoryDto CategoryDto);

  Category getCategoryById(UUID id);

  List<Category> getAllCategories();

  Category updateCategory(UUID id, CategoryDto CategoryDto);

  void deleteCategoryById(UUID id);
}
