package com.iodsky.orderly.service;

import java.util.UUID;

import com.iodsky.orderly.model.CartItem;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.CartItemRepository;
import org.springframework.stereotype.Service;

import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.repository.CartRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductService productService;

  public Cart saveCart(Cart cart) {
    return cartRepository.save(cart);
  }

  public Cart getCart(UUID id) {
    return cartRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found for Id " + id));
  }

  public Cart getCartByUser(User user) {
    return cartRepository.findByUserId(user.getId()).orElseGet(() -> saveCart(new Cart(user)));
  }

  public Cart clearCart(User user) {
    Cart cart = getCartByUser(user);

    cart.getItems().clear();
    return cartRepository.save(cart);
  }

  public CartItem getCartItem(User user, UUID productId) {
    return this.getCartByUser(user).getItems().stream().filter(i -> i.getProduct().getId().equals(productId))
            .findFirst().orElseThrow(() -> new ResourceNotFoundException("Cart item not found for id " + productId));
  }

  public CartItem addItemToCart(User user, UUID productId, int quantity) {
    // Get cart
    Cart cart = this.getCartByUser(user);
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
    this.saveCart(cart);

    return item;
  }

  public void removeItemFromCart(User user, UUID productId) {
    Cart cart = this.getCartByUser(user);
    CartItem item = cart.getItems().stream()
            .filter(i -> i.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for product id " + productId));
    cart.removeItem(item);
    this.saveCart(cart);
  }

  public CartItem updateItemQuantity(User user, UUID productId, int quantity) {
    Cart cart = this.getCartByUser(user);
    CartItem item = cart.getItems().stream()
            .filter(i -> i.getProduct().getId().equals(productId)).findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for product id " + productId));

    if (quantity <= 0) {
      cart.removeItem(item);
      cartItemRepository.delete(item);
    } else {
      item.setQuantity(quantity);
    }

    this.saveCart(cart);
    return item;
  }
}
