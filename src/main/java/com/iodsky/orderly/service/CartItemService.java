package com.iodsky.orderly.service;

import java.util.UUID;

import com.iodsky.orderly.model.User;
import org.springframework.stereotype.Service;

import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.model.CartItem;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.CartItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {

  private final CartItemRepository cartItemRepository;
  private final CartService cartService;
  private final ProductService productService;

  public CartItem getCartItem(UUID cartId, UUID productId, User user) {
    return cartService.getCart(cartId, user).getItems().stream().filter(i -> i.getProduct().getId().equals(productId))
        .findFirst().orElseThrow(() -> new ResourceNotFoundException("Cart item not found for id " + productId));
  }

  public CartItem addItemToCart(UUID productId, int quantity, User user) {
    // Get cart
    Cart cart = cartService.getCartByUser(user);
    // Get product
    Product product = productService.getProduct(productId);

    CartItem cartItem = cart.getItems()
        .stream()
        .filter(i -> i.getProduct().getId().equals(product.getId()))
        .findFirst()
        .orElse(new CartItem());
    // If product does not exist create new cart item else increase quantity
    if (cartItem.getId() == null) {
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(quantity );
      cartItem.setUnitPrice(product.getPrice());
    } else {
      cartItem.setQuantity(cartItem.getQuantity() + quantity);
    }

    cart.addItem(cartItem);
    CartItem item = cartItemRepository.save(cartItem);
    cartService.saveCart(cart);

    return item;
  }

  public void removeItemFromCart(UUID cartId, UUID productId, User user) {
    Cart cart = cartService.getCart(cartId, user);
    CartItem item = cart.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for product id " + productId));
    cart.removeItem(item);
    cartService.saveCart(cart);
  }

  public CartItem updateItemQuantity(UUID cartId, UUID productId, int quantity, User user) {
    Cart cart = cartService.getCart(cartId, user);
    CartItem item = cart.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(productId)).findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for product id " + productId));

    if (quantity <= 0) {
      cart.removeItem(item);
      cartItemRepository.delete(item);
    } else {
      item.setQuantity(quantity);
    }

    cartService.saveCart(cart);
    return item;
  }

}
