package com.iodsky.orderly.service.cart;

import java.util.UUID;

import com.iodsky.orderly.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.iodsky.orderly.exceptions.ResourceNotFoundException;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.repository.CartRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartService implements ICartService {

  private final CartRepository cartRepository;

  @Override
  public Cart saveCart(Cart cart) {
    return cartRepository.save(cart);
  }

  @Override
  public Cart getCart(UUID id, User user) {
    Cart cart = cartRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found for Id " + id));

    if (!cart.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("You cannot access another user's cart");
    }

    return cart;
  }

  @Override
  public Cart getCartByUser(User user) {
    return cartRepository.findByUserId(user.getId()).orElseGet(() -> saveCart(new Cart(user)));
  }

  @Override
  public Cart clearCart(UUID id, User user) {
    Cart cart = getCart(id, user);

    if (!cart.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("You cannot access another user's cart");
    }

    cart.getItems().clear();
    return cartRepository.save(cart);
  }
}
