package com.iodsky.orderly.dto.mapper;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.iodsky.orderly.dto.product.ProductDto;
import com.iodsky.orderly.dto.product.ProductRequestDto;
import com.iodsky.orderly.model.Product;

@Component
public class ProductMapper {
  public static ProductDto toDto(Product product) {
    if (product == null) {
      return null;
    }

    return ProductDto.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice()).stock(product.getStock())
        .category(product.getCategory().getName())
        .images(product.getImages().stream().map(ImageMapper::toDto).toList())
        .build();
  }

  public static Product toEntity(ProductRequestDto dto) {
    if (dto == null) {
      return null;
    }

    return Product.builder()
        .name(dto.getName())
        .description(dto.getDescription())
        .brand(dto.getBrand())
        .price(dto.getPrice())
        .stock(dto.getStock())
        .images(new ArrayList<>())
        .build();
  }
}
