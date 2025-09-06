package com.iodsky.orderly.service.category;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.iodsky.orderly.dto.category.CategoryDto;
import com.iodsky.orderly.dto.mapper.CategoryMapper;
import com.iodsky.orderly.exceptions.DupilcateResoruceException;
import com.iodsky.orderly.exceptions.ResourceInUseException;
import com.iodsky.orderly.exceptions.ResourceNotFoundException;
import com.iodsky.orderly.model.Category;
import com.iodsky.orderly.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

  private final CategoryRepository categoryRepository;

  @Override
  public Category addCategory(CategoryDto categoryDto) {
    try {
      Category category = CategoryMapper.toEntity(categoryDto);

      return categoryRepository.save(category);

    } catch (DataIntegrityViolationException ex) {
      throw new DupilcateResoruceException("Category " + categoryDto.getName() + " already exists.");
    }

  }

  @Override
  public Category getCategoryById(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found for id: " + id));
    return category;
  }

  @Override
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  public Category updateCategory(Long id, CategoryDto categoryDto) {
    try {
      Category existing = categoryRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

      existing.setName(categoryDto.getName());

      return categoryRepository.save(existing);

    } catch (DataIntegrityViolationException ex) {
      throw new DupilcateResoruceException("Category " + categoryDto.getName() + " already exists.");
    }
  }

  @Override
  public void deleteCategoryById(Long id) {
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
