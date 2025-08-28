package com.iodsky.orderly.service.category;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.iodsky.orderly.dto.category.CategoryDto;
import com.iodsky.orderly.exceptions.DupilcateResoruceException;
import com.iodsky.orderly.exceptions.ResourceNotFoundException;
import com.iodsky.orderly.model.Category;
import com.iodsky.orderly.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

  private final CategoryRepository categoryRepository;
  private final ModelMapper modelMapper;

  @Override
  public CategoryDto addCategory(CategoryDto categoryDto) {
    try {
      Category category = modelMapper.map(categoryDto, Category.class);

      Category saved = categoryRepository.save(category);

      return modelMapper.map(saved, CategoryDto.class);
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
  public List<CategoryDto> getAllCategories() {
    return categoryRepository.findAll()
        .stream()
        .map(p -> modelMapper.map(p, CategoryDto.class))
        .toList();
  }

  @Override
  public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
    try {
      Category existing = categoryRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

      existing.setName(categoryDto.getName());

      Category saved = categoryRepository.save(existing);

      return modelMapper.map(saved, CategoryDto.class);
    } catch (DataIntegrityViolationException ex) {
      throw new DupilcateResoruceException("Category " + categoryDto.getName() + " already exists.");
    }
  }

  @Override
  public void deleteCategoryById(Long id) {
    categoryRepository.findById(id).ifPresentOrElse(categoryRepository::delete,
        () -> {
          throw new ResourceNotFoundException("Category not found");
        });
  }

}
