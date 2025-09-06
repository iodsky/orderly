package com.iodsky.orderly.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.iodsky.orderly.dto.image.ImageDto;

@Data
@Builder
public class ProductDto {
    private long id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private int stock;

    private String category;
    private List<ImageDto> images;
}
