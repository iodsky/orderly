package com.iodsky.orderly.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iodsky.orderly.dto.category.CategoryDto;
import com.iodsky.orderly.dto.mapper.CategoryMapper;
import com.iodsky.orderly.service.category.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping
  public ResponseEntity<CategoryDto> createCategory(@Valid() @RequestBody CategoryDto categoryDto) {
    CategoryDto category = CategoryMapper.toDto(categoryService.addCategory(categoryDto));

    return new ResponseEntity<>(category, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<CategoryDto>> getCategories() {
    List<CategoryDto> categories = categoryService.getAllCategories().stream().map(CategoryMapper::toDto).toList();

    return ResponseEntity.ok(categories);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDto> updateCategory(@PathVariable() UUID id,
      @Valid() @RequestBody() CategoryDto categoryDto) {
    CategoryDto category = CategoryMapper.toDto(categoryService.updateCategory(id, categoryDto));

    return ResponseEntity.ok(category);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteProduct(@PathVariable() UUID id) {
    categoryService.deleteCategoryById(id);
    return ResponseEntity.ok("Category deleted successfully");
  }
}
