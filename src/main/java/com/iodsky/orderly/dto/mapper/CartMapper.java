package com.iodsky.orderly.dto.mapper;

import org.springframework.stereotype.Component;

import com.iodsky.orderly.dto.CartDto;
import com.iodsky.orderly.model.Cart;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartMapper {

  private final CartItemMapper cartItemMapper;

  public CartDto toDto(Cart cart) {
    if (cart == null) {
      return null;
    }

    return CartDto.builder()
        .id(cart.getId())
        .items(cart.getItems().stream().map(cartItemMapper::toDto).toList())
        .totalAmount(cart.getTotalAmount())
        .build();
  }
}
