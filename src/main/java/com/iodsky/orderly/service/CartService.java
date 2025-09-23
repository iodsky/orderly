package com.iodsky.orderly.service;

import java.util.UUID;

import com.iodsky.orderly.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.repository.CartRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartService {

  private final CartRepository cartRepository;

  public Cart saveCart(Cart cart) {
    return cartRepository.save(cart);
  }

  public Cart getCart(UUID id, User user) {
    Cart cart = cartRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found for Id " + id));

    if (!cart.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("You cannot access another user's cart");
    }

    return cart;
  }

  public Cart getCartByUser(User user) {
    return cartRepository.findByUserId(user.getId()).orElseGet(() -> saveCart(new Cart(user)));
  }

  public Cart clearCart(UUID id, User user) {
    Cart cart = getCart(id, user);

    if (!cart.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("You cannot access another user's cart");
    }

    cart.getItems().clear();
    return cartRepository.save(cart);
  }
}
