package com.iodsky.orderly.controller;

import java.util.UUID;

import com.iodsky.orderly.model.User;
import com.iodsky.orderly.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iodsky.orderly.dto.CartItemDto;
import com.iodsky.orderly.dto.mapper.CartItemMapper;
import com.iodsky.orderly.model.CartItem;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/carts/items")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CartItemController {

  private final CartItemService cartItemService;
  private final CartItemMapper cartItemMapper;

  @Operation(
          summary = "Adds a product with an optional quantity to the authenticated user's cart."
  )
  @PostMapping("/{productId}")
  public ResponseEntity<CartItemDto> addItemToCart(
      @PathVariable UUID productId,
      @RequestParam(required = false, defaultValue = "1") int quantity,
      @AuthenticationPrincipal User user) {

    CartItem item = cartItemService.addItemToCart(productId, quantity, user);
    return ResponseEntity.ok(cartItemMapper.toDto(item));
  }

  @Operation(
          summary = "Fetches a cart item by cart ID and product ID for the authenticated user."
  )
  @GetMapping("/{productId}")
  public ResponseEntity<CartItemDto> getCartItem(
      @RequestParam UUID cartId,
      @PathVariable UUID productId,
      @AuthenticationPrincipal User user) {
    CartItem item = cartItemService.getCartItem(cartId, productId, user);
    return ResponseEntity.ok(cartItemMapper.toDto(item));
  }

  @Operation(
          summary = "Updates the quantity of a cart item for the authenticated user by cart and product ID."
  )
  @PutMapping("/{productId}")
  public ResponseEntity<CartItemDto> updateItemQuantity(
      @PathVariable UUID productId,
      @RequestParam UUID cartId,
      @RequestParam int quantity,
      @AuthenticationPrincipal User user) {
    CartItem item = cartItemService.updateItemQuantity(cartId, productId, quantity, user);
    return ResponseEntity.ok(cartItemMapper.toDto(item));
  }


  @Operation(
          summary = "Removes a cart item for the authenticated user by cart and product ID."
  )
  @DeleteMapping("/{productId}")
  public ResponseEntity<String> removeItem(@RequestParam UUID cartId, @PathVariable UUID productId, @AuthenticationPrincipal User user) {
    cartItemService.removeItemFromCart(cartId, productId, user);
    return ResponseEntity.ok("Item removed from cart");
  }

}
