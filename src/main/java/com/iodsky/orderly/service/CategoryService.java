package com.iodsky.orderly.service;

import java.util.List;
import java.util.UUID;

import com.iodsky.orderly.exception.DuplicateResourceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.iodsky.orderly.dto.CategoryDto;
import com.iodsky.orderly.dto.mapper.CategoryMapper;
import com.iodsky.orderly.exception.ResourceInUseException;
import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Category;
import com.iodsky.orderly.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService  {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public Category addCategory(CategoryDto categoryDto) {
    try {
      Category category = categoryMapper.toEntity(categoryDto);

      return categoryRepository.save(category);

    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateResourceException("Category " + categoryDto.getName() + " already exists.");
    }

  }

  public Category getCategoryById(UUID id) {
      return categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found for id: " + id));
  }

  public Category getOrCreateCategory(String name) {
    return categoryRepository.findByName(name)
            .orElseGet(() -> categoryRepository
                    .save(Category.builder().name(name).build()));
  }

  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  public Category updateCategory(UUID id, CategoryDto categoryDto) {
    try {
      Category existing = getCategoryById(id);

      existing.setName(categoryDto.getName());

      return categoryRepository.save(existing);

    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateResourceException("Category " + categoryDto.getName() + " already exists.");
    }
  }

  public void deleteCategoryById(UUID id) {
    try {
      categoryRepository.findById(id).ifPresentOrElse(categoryRepository::delete,
          () -> {
            throw new ResourceNotFoundException("Category not found");
          });
    } catch (DataIntegrityViolationException e) {
      throw new ResourceInUseException("Category cannot be deleted because it has associated products");
    }

  }

}
