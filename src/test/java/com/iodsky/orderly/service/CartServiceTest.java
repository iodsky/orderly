package com.iodsky.orderly.service;

import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.model.CartItem;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.CartItemRepository;
import com.iodsky.orderly.repository.CartRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductService productService;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    private UUID cartId;
    private User user;
    private Cart cart;
    private CartItem cartItem;
    private Product product;
    private Product newProduct;

    @BeforeEach
    void setup() {
        cartId = UUID.randomUUID();

        user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .build();

        cart = Cart.builder()
                .id(cartId)
                .user(user)
                .items(new HashSet<>())
                .build();

        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Product")
                .description("Description")
                .brand("Brand")
                .stock(10)
                .price(BigDecimal.valueOf(179))
                .build();

        cartItem = CartItem.builder()
                .id(UUID.randomUUID())
                .cart(cart)
                .product(product)
                .quantity(1)
                .build();

        cart.addItem(cartItem);

        newProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("New Product")
                .description("Description")
                .brand("New Brand")
                .stock(12)
                .price(BigDecimal.valueOf(130))
                .build();
    }

    @Nested
    @DisplayName("Create cart tests")
    class saveCartTests {
        @Test
        void shouldSaveCart() {
            when(cartRepository.save(cart)).thenReturn(cart);

            Cart result = cartService.saveCart(cart);

            assertNotNull(result);
            assertEquals(cart, result);

            verify(cartRepository).save(cart);
        }
    }

    @Nested
    @DisplayName("Find cart tests")
    class getCartTests {

        @Test
        void shouldReturnCartIfUserOwnsIt() {
            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

            Cart result = cartService.getCart(cartId);

            assertNotNull(result);
            assertEquals(cart, result);
            assertEquals(user.getId(), result.getUser().getId());

            verify(cartRepository).findById(cartId);
        }

        @Test
        void shouldThrowExceptionIfCartNotFound() {
            when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.getCart(cartId)
            );

            assertTrue(ex.getMessage().contains(cartId.toString()));
            verify(cartRepository).findById(cartId);
        }

    }

    @Nested
    @DisplayName("Get cart by user tests")
    class getCartByUserTests {

        @Test
        void shouldReturnCartIfExists() {
            when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

            Cart result = cartService.getCartByUser(user);

            assertNotNull(result);
            assertEquals(cart, result);

            verify(cartRepository).findByUserId(user.getId());
        }

        @Test
        void shouldCreateNewCartIfNoneExists() {
            when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
            when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

            Cart result = cartService.getCartByUser(user);

            assertNotNull(result);
            assertEquals(user.getId(), result.getUser().getId());

            verify(cartRepository).findByUserId(user.getId());
            verify(cartRepository).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Clear cart tests")
    class clearCartTests {

        @Test
        void shouldClearCartIfOwnedByUser() {
            cart.getItems().add(new CartItem());
            cart.getItems().add(new CartItem());

            when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
            when(cartRepository.save(cart)).thenAnswer(inv -> inv.getArgument(0));

            Cart result = cartService.clearCart(user);

            assertNotNull(result);
            assertTrue(result.getItems().isEmpty());

            verify(cartRepository).findByUserId(user.getId());
            verify(cartRepository).save(cart);
        }

    }

    @Nested
    @DisplayName("Get cart item tests")
    class GetCartItemTests {

        @Test
        void shouldReturnCartItemIfFound() {
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cart));

            CartItem result = cartService.getCartItem(user, product.getId());

            assertNotNull(result);
            assertEquals(product.getId(), result.getProduct().getId());
            verify(cartRepository).findByUserId(user.getId());
        }

        @Test
        void shouldThrowIfCartItemNotFound() {
            cart.getItems().clear();
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cart));

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.getCartItem(user, cartId)
            );
        }
    }

    @Nested
    @DisplayName("Add item to cart tests")
    class AddItemToCartTests {

        @Test
        void shouldAddNewItemIfNotExists() {
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cart));
            when(productService.getProduct(newProduct.getId()))
                    .thenReturn(newProduct);
            when(cartItemRepository.save(any(CartItem.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(cartRepository.save(cart))
                    .thenReturn(cart);

            CartItem result = cartService.addItemToCart(user, newProduct.getId(), 2);

            assertNotNull(result);
            assertEquals(2, result.getQuantity());
            assertEquals(newProduct, result.getProduct());
            assertEquals(newProduct.getPrice(), result.getUnitPrice());

            verify(cartRepository).findByUserId(user.getId());
            verify(productService).getProduct(newProduct.getId());
            verify(cartItemRepository).save(any(CartItem.class));
            verify(cartRepository).save(cart);
        }

        @Test
        void shouldIncreaseQuantityIfItemAlreadyExists() {
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cart));
            when(productService.getProduct(product.getId()))
                    .thenReturn(product);
            when(cartItemRepository.save(cartItem))
                    .thenReturn(cartItem);
            when(cartRepository.save(cart))
                    .thenReturn(cart);

            CartItem result = cartService.addItemToCart(user, product.getId(), 3);

            assertEquals(4, result.getQuantity());
            verify(cartItemRepository).save(cartItem);
            verify(cartRepository).save(cart);
        }
    }

    @Nested
    @DisplayName("Remove item from cart tests")
    class RemoveItemFromCartTests {

        @Test
        void shouldRemoveItemIfExists() {
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class)))
                    .thenReturn(cart);

            cartService.removeItemFromCart(user, product.getId());

            assertTrue(cart.getItems().isEmpty());
            verify(cartRepository).findByUserId(user.getId());
            verify(cartRepository).save(cart);
        }

        @Test
        void shouldThrowExceptionIfItemNotFound() {
            cart.getItems().clear();
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cart));

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.removeItemFromCart(user, product.getId())
            );
        }
    }

    @Nested
    @DisplayName("Update item quantity tests")
    class UpdateItemQuantityTests {

        @Test
        void shouldUpdateQuantityIfGreaterThanZero() {
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cart));
            when(cartRepository.save(cart))
                    .thenReturn(cart);

            CartItem result = cartService.updateItemQuantity(user, product.getId(), 5);

            assertEquals(5, result.getQuantity());
            verify(cartRepository).findByUserId(user.getId());
            verify(cartRepository).save(cart);
        }

        @Test
        void shouldRemoveItemIfQuantityIsZeroOrLess() {
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cart));
            when(cartRepository.save(cart))
                    .thenReturn(cart);

            CartItem result = cartService.updateItemQuantity(user, product.getId(), 0);

            assertFalse(cart.getItems().contains(result));
            verify(cartItemRepository).delete(cartItem);
            verify(cartRepository).save(cart);
        }

        @Test
        void shouldThrowExceptionIfItemNotFound() {
            cart.getItems().clear();
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cart));

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.updateItemQuantity(user, product.getId(), anyInt())
            );
        }
    }
}