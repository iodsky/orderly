package com.iodsky.orderly.service.cart;

import java.util.UUID;

import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.model.User;

public interface ICartService {

  Cart saveCart(Cart cart);

  Cart getCart(UUID id, User user);

  Cart clearCart(UUID id, User user);

}
