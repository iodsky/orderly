package com.iodsky.orderly.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;
  @NotBlank(message = "Category name is required")
  private String name;
}
