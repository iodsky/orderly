package com.iodsky.orderly.dto.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartDto {
  private UUID id;
  private List<CartItemDto> items;
  private BigDecimal totalAmount;
}
