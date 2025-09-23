package com.iodsky.orderly.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private UUID id;
  @NotBlank(message = "Category name is required")
  private String name;
}
