package com.iodsky.orderly.service.cartItem;

import java.util.UUID;

import com.iodsky.orderly.model.CartItem;

public interface ICartItemService {

  CartItem getCartItem(UUID cartId, UUID productId);

  CartItem addItemToCart(UUID cartId, UUID productId, int quantity);

  void removeItemFromCart(UUID cartId, UUID productId);

  CartItem updateItemQuantity(UUID cartId, UUID productId, int quantity);
}
