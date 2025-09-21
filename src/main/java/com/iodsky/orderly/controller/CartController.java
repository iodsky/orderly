package com.iodsky.orderly.controller;

import java.util.UUID;

import com.iodsky.orderly.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iodsky.orderly.dto.cart.CartDto;
import com.iodsky.orderly.dto.mapper.CartMapper;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.service.cart.ICartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

  private final ICartService cartService;
  private final CartMapper cartMapper;

  @GetMapping("{id}")
  public ResponseEntity<CartDto> getCart(@PathVariable UUID id, @AuthenticationPrincipal User user) {
    Cart cart = cartService.getCart(id, user);
    return ResponseEntity.ok(cartMapper.toDto(cart));
  }

  @PutMapping("{id}/clear")
  public ResponseEntity<CartDto> clearCart(@PathVariable UUID id, @AuthenticationPrincipal User user) {
    Cart cart = cartService.clearCart(id, user);
    return ResponseEntity.ok(cartMapper.toDto(cart));
  }

}
