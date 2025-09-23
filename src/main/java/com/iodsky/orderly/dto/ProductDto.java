package com.iodsky.orderly.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductDto {
    private UUID id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private int stock;
    private String category;
    private List<ImageDto> images;
}
