package com.iodsky.orderly.controller;

import java.util.UUID;

import com.iodsky.orderly.dto.CartItemDto;
import com.iodsky.orderly.dto.mapper.CartItemMapper;
import com.iodsky.orderly.model.CartItem;
import com.iodsky.orderly.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    private final CartItemMapper cartItemMapper;

    @Operation(
            summary = "Fetches the cart associated to the authenticated user."
    )
    @GetMapping()
    public ResponseEntity<CartDto> getUserCart() {
        Cart cart = cartService.getUserCart();
        return ResponseEntity.ok(cartMapper.toDto(cart));
    }

    @Operation(
            summary = "Fetches the cart by its ID. Only ADMINS can perform this action."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID id) {
        Cart cart = cartService.getCart(id);
        return ResponseEntity.ok(cartMapper.toDto(cart));
    }

    @Operation(
            summary = "Clears the cart of the authenticated user."
    )
    @PutMapping("clear")
    public ResponseEntity<CartDto> clearCart() {
        Cart cart = cartService.clearCart();
        return ResponseEntity.ok(cartMapper.toDto(cart));
    }

    @Operation(
            summary = "Adds a product with an optional quantity to the authenticated user's cart."
    )
    @PostMapping("items/{productId}")
    public ResponseEntity<CartItemDto> addItemToCart(
            @PathVariable UUID productId,
            @RequestParam(required = false, defaultValue = "1") int quantity) {

        CartItem item = cartService.addItemToCart(productId, quantity);
        return ResponseEntity.ok(cartItemMapper.toDto(item));
    }

    @Operation(
            summary = "Fetches a cart item by cart Id for the authenticated user."
    )
    @GetMapping("items/{productId}")
    public ResponseEntity<CartItemDto> getCartItem(@PathVariable UUID productId) {
        CartItem item = cartService.getCartItem(productId);
        return ResponseEntity.ok(cartItemMapper.toDto(item));
    }

    @Operation(
            summary = "Updates the quantity of a cart item for the authenticated user by cart and product ID."
    )
    @PutMapping("items/{productId}")
    public ResponseEntity<CartItemDto> updateItemQuantity(
            @PathVariable UUID productId,
            @RequestParam int quantity) {
        CartItem item = cartService.updateItemQuantity(productId, quantity);
        return ResponseEntity.ok(cartItemMapper.toDto(item));
    }

    @Operation(
            summary = "Removes a cart item for the authenticated user by cart and product ID."
    )
    @DeleteMapping("items/{productId}")
    public ResponseEntity<String> removeItem(
            @PathVariable UUID productId) {
        cartService.removeItemFromCart(productId);
        return ResponseEntity.ok("Item removed from cart");
    }

}
