package com.iodsky.orderly.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDto {
  private UUID id;
  private UUID cartId;
  private UUID productId;
  private String productName;
  private int quantity;
  private BigDecimal unitPrice;
}
