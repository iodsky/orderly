package com.iodsky.orderly.service.cart;

import java.util.UUID;

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
  public Cart getCart(UUID id) {
    return cartRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found for Id " + id));
  }

  @Override
  public Cart clearCart(UUID id) {
    Cart cart = getCart(id);
    cart.getItems().clear();
    return cartRepository.save(cart);
  }
}
