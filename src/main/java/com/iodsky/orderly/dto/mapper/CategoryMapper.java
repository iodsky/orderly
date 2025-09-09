package com.iodsky.orderly.dto.mapper;

import org.springframework.stereotype.Component;

import com.iodsky.orderly.dto.category.CategoryDto;
import com.iodsky.orderly.model.Category;

@Component
public class CategoryMapper {

  public CategoryDto toDto(Category category) {
    if (category == null) {
      return null;
    }

    return CategoryDto.builder()
        .id(category.getId())
        .name(category.getName())
        .build();
  }

  public Category toEntity(CategoryDto dto) {
    if (dto == null) {
      return null;
    }

    return Category.builder().name(dto.getName()).build();
  }
}
