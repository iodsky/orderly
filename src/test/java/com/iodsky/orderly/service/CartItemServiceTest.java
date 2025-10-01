package com.iodsky.orderly.service;

import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.model.CartItem;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartItemService cartItemService;

    private UUID cartId;
    private UUID productId;
    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setup() {
        cartId = UUID.randomUUID();
        productId = UUID.randomUUID();

        user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .build();

        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .build();

        cart = Cart.builder()
                .id(cartId)
                .user(user)
                .items(new HashSet<>())
                .build();

        cartItem = CartItem.builder()
                .id(UUID.randomUUID())
                .product(product)
                .cart(cart)
                .quantity(2)
                .unitPrice(product.getPrice())
                .build();

        cart.getItems().add(cartItem);
    }

    @Nested
    @DisplayName("Get cart item tests")
    class GetCartItemTests {

        @Test
        void shouldReturnCartItemIfFound() {
            when(cartService.getCart(cartId, user)).thenReturn(cart);

            CartItem result = cartItemService.getCartItem(cartId, productId, user);

            assertNotNull(result);
            assertEquals(productId, result.getProduct().getId());
            verify(cartService).getCart(cartId, user);
        }

        @Test
        void shouldThrowIfCartItemNotFound() {
            cart.getItems().clear();
            when(cartService.getCart(cartId, user)).thenReturn(cart);

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartItemService.getCartItem(cartId, productId, user)
            );
        }
    }

    @Nested
    @DisplayName("Add item to cart tests")
    class AddItemToCartTests {

        @Test
        void shouldAddNewItemIfNotExists() {
            cart.getItems().clear();
            when(cartService.getCartByUser(user)).thenReturn(cart);
            when(productService.getProduct(productId)).thenReturn(product);
            when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> {
                CartItem ci = inv.getArgument(0);
                ci.setId(UUID.randomUUID());
                return ci;
            });
            when(cartService.saveCart(cart)).thenReturn(cart);

            CartItem result = cartItemService.addItemToCart(productId, 3, user);

            assertNotNull(result.getId());
            assertEquals(3, result.getQuantity());
            assertEquals(product, result.getProduct());
            assertEquals(product.getPrice(), result.getUnitPrice());

            verify(cartService).getCartByUser(user);
            verify(productService).getProduct(productId);
            verify(cartItemRepository).save(any(CartItem.class));
            verify(cartService).saveCart(cart);
        }

        @Test
        void shouldIncreaseQuantityIfItemAlreadyExists() {
            when(cartService.getCartByUser(user)).thenReturn(cart);
            when(productService.getProduct(productId)).thenReturn(product);
            when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
            when(cartService.saveCart(cart)).thenReturn(cart);

            CartItem result = cartItemService.addItemToCart(productId, 2, user);

            assertEquals(4, result.getQuantity());
            verify(cartItemRepository).save(cartItem);
            verify(cartService).saveCart(cart);
        }
    }

    @Nested
    @DisplayName("Remove item from cart tests")
    class RemoveItemFromCartTests {

        @Test
        void shouldRemoveItemIfExists() {
            when(cartService.getCart(cartId, user)).thenReturn(cart);
            when(cartService.saveCart(cart)).thenReturn(cart);

            cartItemService.removeItemFromCart(cartId, productId, user);

            assertTrue(cart.getItems().isEmpty());
            verify(cartService).getCart(cartId, user);
            verify(cartService).saveCart(cart);
        }

        @Test
        void shouldThrowExceptionIfItemNotFound() {
            cart.getItems().clear();
            when(cartService.getCart(cartId, user)).thenReturn(cart);

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartItemService.removeItemFromCart(cartId, productId, user)
            );
        }
    }

    @Nested
    @DisplayName("Update item quantity tests")
    class UpdateItemQuantityTests {

        @Test
        void shouldUpdateQuantityIfGreaterThanZero() {
            when(cartService.getCart(cartId, user)).thenReturn(cart);
            when(cartService.saveCart(cart)).thenReturn(cart);

            CartItem result = cartItemService.updateItemQuantity(cartId, productId, 5, user);

            assertEquals(5, result.getQuantity());
            verify(cartService).getCart(cartId, user);
            verify(cartService).saveCart(cart);
        }

        @Test
        void shouldRemoveItemIfQuantityIsZeroOrLess() {
            when(cartService.getCart(cartId, user)).thenReturn(cart);
            when(cartService.saveCart(cart)).thenReturn(cart);

            CartItem result = cartItemService.updateItemQuantity(cartId, productId, 0, user);

            assertFalse(cart.getItems().contains(result));
            verify(cartItemRepository).delete(cartItem);
            verify(cartService).saveCart(cart);
        }

        @Test
        void shouldThrowExceptionIfItemNotFound() {
            cart.getItems().clear();
            when(cartService.getCart(cartId, user)).thenReturn(cart);

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartItemService.updateItemQuantity(cartId, productId, 3, user)
            );
        }
    }
}