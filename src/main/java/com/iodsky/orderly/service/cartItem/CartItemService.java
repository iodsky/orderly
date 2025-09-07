package com.iodsky.orderly.service.cartItem;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.iodsky.orderly.exceptions.ResourceNotFoundException;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.model.CartItem;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.CartItemRepository;
import com.iodsky.orderly.repository.CartRepository;
import com.iodsky.orderly.service.cart.ICartService;
import com.iodsky.orderly.service.product.IProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

  private final CartItemRepository cartItemRepository;
  private final CartRepository cartRepository;
  private final ICartService cartService;
  private final IProductService productService;

  @Override
  public CartItem getCartItem(UUID cartId, UUID productId) {
    return cartService.getCart(productId).getItems().stream().filter(i -> i.getProduct().getId().equals(productId))
        .findFirst().orElseThrow(() -> new ResourceNotFoundException("Cart item not found for id " + productId));
  }

  @Override
  public CartItem addItemToCart(UUID cartId, UUID productId, int quantity) {
    // Get cart
    Cart cart = (cartId == null)
        ? cartRepository.save(new Cart())
        : cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found for id " + cartId));
    // Get product
    Product product = productService.getProduct(productId);

    CartItem cartItem = cart.getItems()
        .stream()
        .filter(i -> i.getProduct().getId().equals(product.getId()))
        .findFirst()
        .orElse(new CartItem());
    // If product does not exists create new cart item else increase quantity
    if (cartItem.getId() == null) {
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(quantity);
      cartItem.setUnitPrice(product.getPrice());
    } else {
      cartItem.setQuantity(cartItem.getQuantity() + quantity);
    }

    cart.addItem(cartItem);
    CartItem item = cartItemRepository.save(cartItem);
    cartRepository.save(cart);

    return item;
  }

  @Override
  public void removeItemFromCart(UUID cartId, UUID productId) {
    Cart cart = cartService.getCart(cartId);
    CartItem item = cart.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for product id " + productId));
    cart.removeItem(item);
    cartRepository.save(cart);
  }

  @Override
  public CartItem updateItemQuantity(UUID cartId, UUID productId, int quantity) {
    Cart cart = cartService.getCart(cartId);
    CartItem item = cart.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(productId)).findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for product id " + productId));

    if (quantity <= 0) {
      cart.removeItem(item);
      cartItemRepository.delete(item);
    } else {
      item.setQuantity(quantity);
    }

    cartRepository.save(cart);
    return item;
  }

}
