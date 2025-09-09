package com.iodsky.orderly.service.cart;

import java.util.UUID;

import com.iodsky.orderly.model.Cart;

public interface ICartService {

  Cart saveCart(Cart cart);

  Cart getCart(UUID id);

  Cart clearCart(UUID id);

}
