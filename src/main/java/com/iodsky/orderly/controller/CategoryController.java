package com.iodsky.orderly.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iodsky.orderly.dto.CategoryDto;
import com.iodsky.orderly.dto.mapper.CategoryMapper;
import com.iodsky.orderly.service.CategoryService;

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
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  @Operation(
          summary = "Create a product category. Only Admin can perform this action."
  )
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryDto> createCategory(@Valid() @RequestBody CategoryDto categoryDto) {
    CategoryDto category = categoryMapper.toDto(categoryService.addCategory(categoryDto));

    return new ResponseEntity<>(category, HttpStatus.CREATED);
  }

  @Operation(
          summary = "Fetches all product categories. Only Admin can perform this action."
  )
  @GetMapping
  public ResponseEntity<List<CategoryDto>> getCategories() {
    List<CategoryDto> categories = categoryService.getAllCategories().stream().map(categoryMapper::toDto).toList();

    return ResponseEntity.ok(categories);
  }

  @Operation(
          summary = "Updates a category by category ID. Only Admin can perform this action."
  )
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryDto> updateCategory(@PathVariable() UUID id,
      @Valid() @RequestBody() CategoryDto categoryDto) {
    CategoryDto category = categoryMapper.toDto(categoryService.updateCategory(id, categoryDto));

    return ResponseEntity.ok(category);
  }

  @Operation(
          summary = "Removes a category by category ID. Only Admin can perform this action."
  )
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> deleteProduct(@PathVariable() UUID id) {
    categoryService.deleteCategoryById(id);
    return ResponseEntity.ok("Category deleted successfully");
  }
}
