package com.iodsky.orderly.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private long id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private int stock;

    private String category;
}
