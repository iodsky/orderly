package com.iodsky.orderly.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iodsky.orderly.dto.category.CategoryDto;
import com.iodsky.orderly.service.category.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
    CategoryDto saved = categoryService.addCategory(categoryDto);

    return new ResponseEntity<>(saved, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<CategoryDto>> getCategories() {
    List<CategoryDto> categories = categoryService.getAllCategories();

    return ResponseEntity.ok(categories);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDto> updateCategory(@PathVariable() Long id,
      @Valid() @RequestBody() CategoryDto categoryDto) {
    CategoryDto category = categoryService.updateCategory(id, categoryDto);

    return ResponseEntity.ok(category);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteProduct(@PathVariable() Long id) {
    categoryService.deleteCategoryById(id);
    return ResponseEntity.ok("Category deleted successfully");
  }
}
