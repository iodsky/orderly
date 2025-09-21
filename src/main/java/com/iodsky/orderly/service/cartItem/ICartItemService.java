package com.iodsky.orderly.service.cartItem;

import java.util.UUID;

import com.iodsky.orderly.model.CartItem;
import com.iodsky.orderly.model.User;

public interface ICartItemService {

  CartItem addItemToCart(UUID cartId, UUID productId, int quantity, User user);

  CartItem getCartItem(UUID cartId, UUID productId, User user);

  CartItem updateItemQuantity(UUID cartId, UUID productId, int quantity, User user);

  void removeItemFromCart(UUID cartId, UUID productId, User user);

}
