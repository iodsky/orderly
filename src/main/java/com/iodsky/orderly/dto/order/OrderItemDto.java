package com.iodsky.orderly.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderItemDto {
    private UUID id;

    private UUID productId;
    private String productName;

    private int quantity;
    private BigDecimal price;
}
