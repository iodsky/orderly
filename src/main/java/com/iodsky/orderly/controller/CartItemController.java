package com.iodsky.orderly.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iodsky.orderly.dto.cart.CartItemDto;
import com.iodsky.orderly.dto.mapper.CartItemMapper;
import com.iodsky.orderly.model.CartItem;
import com.iodsky.orderly.service.cartItem.ICartItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/carts/items")
@RequiredArgsConstructor
public class CartItemController {

  private final ICartItemService cartItemService;
  private final CartItemMapper cartItemMapper;

  @PostMapping("/{productId}")
  public ResponseEntity<CartItemDto> addItemToCart(
      @PathVariable UUID productId,
      @RequestParam(required = false) UUID cartId,
      @RequestParam int quantity) {

    CartItem item = cartItemService.addItemToCart(cartId, productId, quantity);
    return ResponseEntity.ok(cartItemMapper.toDto(item));
  }

  @GetMapping("/{productId}")
  public ResponseEntity<CartItemDto> getCartItem(
      @PathVariable UUID cartId,
      @PathVariable UUID productId) {
    CartItem item = cartItemService.getCartItem(cartId, productId);
    return ResponseEntity.ok(cartItemMapper.toDto(item));
  }

  @PutMapping("/{productId}")
  public ResponseEntity<CartItemDto> updateItemQuantity(
      @PathVariable UUID productId,
      @RequestParam UUID cartId,
      @RequestParam int quantity) {
    CartItem item = cartItemService.updateItemQuantity(cartId, productId, quantity);
    return ResponseEntity.ok(cartItemMapper.toDto(item));
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<String> removeItem(@RequestParam UUID cartId, @PathVariable UUID productId) {
    cartItemService.removeItemFromCart(cartId, productId);
    return ResponseEntity.ok("Item removed from cart");
  }
}
