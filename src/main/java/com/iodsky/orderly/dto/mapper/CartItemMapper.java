package com.iodsky.orderly.dto.mapper;

import org.springframework.stereotype.Component;

import com.iodsky.orderly.dto.CartItemDto;
import com.iodsky.orderly.model.CartItem;

@Component
public class CartItemMapper {

  public CartItemDto toDto(CartItem cartItem) {
    if (cartItem == null) {
      return null;
    }

    return CartItemDto.builder()
        .id(cartItem.getId())
        .cartId(cartItem.getCart().getId())
        .productId(cartItem.getProduct().getId())
        .productName(cartItem.getProduct().getName())
        .quantity(cartItem.getQuantity())
        .unitPrice(cartItem.getUnitPrice())
        .build();
  }

}
