package com.iodsky.orderly.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddProductRequest {
  @NotBlank(message = "Product name is required")
  private String name;

  @NotBlank(message = "Product description is required")
  private String description;

  @NotBlank(message = "Product brand is required")
  private String brand;

  @NotNull(message = "Product price is required")
  @Positive(message = "Price must be positive")
  private BigDecimal price;

  @Min(value = 0, message = "Stock cannot be less than 0")
  private int stock;

  @NotBlank(message = "Category is required")
  private String category;
}
