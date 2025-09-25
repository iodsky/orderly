package com.iodsky.orderly.controller;

import java.util.UUID;

import com.iodsky.orderly.model.User;
import com.iodsky.orderly.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iodsky.orderly.dto.CartDto;
import com.iodsky.orderly.dto.mapper.CartMapper;
import com.iodsky.orderly.model.Cart;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/carts")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;
  private final CartMapper cartMapper;

  @Operation(
          summary = "Fetches the cart by its ID. Customers can access only their own cart, while admins can access any cart."
  )
  @GetMapping("{id}")
  public ResponseEntity<CartDto> getCart(@PathVariable UUID id, @AuthenticationPrincipal User user) {
    Cart cart = cartService.getCart(id, user);
    return ResponseEntity.ok(cartMapper.toDto(cart));
  }

  @Operation(
          summary = "Clears the cart with the given ID. Only the associated user can perform this action."
  )
  @PutMapping("{id}/clear")
  public ResponseEntity<CartDto> clearCart(@PathVariable UUID id, @AuthenticationPrincipal User user) {
    Cart cart = cartService.clearCart(id, user);
    return ResponseEntity.ok(cartMapper.toDto(cart));
  }

}
