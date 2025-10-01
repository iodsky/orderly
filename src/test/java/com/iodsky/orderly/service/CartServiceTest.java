package com.iodsky.orderly.service;

import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Cart;
import com.iodsky.orderly.model.CartItem;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

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

    @InjectMocks
    private CartService cartService;

    private UUID cartId;
    private User user;
    private User otherUser;
    private Cart existingCart;

    @BeforeEach
    void setup() {
        cartId = UUID.randomUUID();

        user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .build();

        otherUser = User.builder()
                .id(UUID.randomUUID())
                .username("otherUser")
                .build();

        existingCart = Cart.builder()
                .id(cartId)
                .user(user)
                .items(new HashSet<>())
                .build();
    }

    @Nested
    @DisplayName("Create cart tests")
    class saveCartTests {
        @Test
        void shouldSaveCart() {
            when(cartRepository.save(existingCart)).thenReturn(existingCart);

            Cart result = cartService.saveCart(existingCart);

            assertNotNull(result);
            assertEquals(existingCart, result);

            verify(cartRepository).save(existingCart);
        }
    }

    @Nested
    @DisplayName("Find cart tests")
    class getCartTests {

        @Test
        void shouldReturnCartIfUserOwnsIt() {
            when(cartRepository.findById(cartId)).thenReturn(Optional.of(existingCart));

            Cart result = cartService.getCart(cartId, user);

            assertNotNull(result);
            assertEquals(existingCart, result);
            assertEquals(user.getId(), result.getUser().getId());

            verify(cartRepository).findById(cartId);
        }

        @Test
        void shouldThrowExceptionIfCartNotFound() {
            when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.getCart(cartId, user)
            );

            assertTrue(ex.getMessage().contains(cartId.toString()));
            verify(cartRepository).findById(cartId);
        }

        @Test
        void shouldThrowExceptionIfCartBelongsToAnotherUser() {
            existingCart.setUser(otherUser);
            when(cartRepository.findById(cartId)).thenReturn(Optional.of(existingCart));

            assertThrows(
                    AccessDeniedException.class,
                    () -> cartService.getCart(cartId, user)
            );

            verify(cartRepository).findById(cartId);
        }
    }

    @Nested
    @DisplayName("Get cart by user tests")
    class getCartByUserTests {

        @Test
        void shouldReturnCartIfExists() {
            when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(existingCart));

            Cart result = cartService.getCartByUser(user);

            assertNotNull(result);
            assertEquals(existingCart, result);

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
            existingCart.getItems().add(new CartItem());
            existingCart.getItems().add(new CartItem());

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(existingCart));
            when(cartRepository.save(existingCart)).thenAnswer(inv -> inv.getArgument(0));

            Cart result = cartService.clearCart(cartId, user);

            assertNotNull(result);
            assertTrue(result.getItems().isEmpty());

            verify(cartRepository).findById(cartId);
            verify(cartRepository).save(existingCart);
        }

        @Test
        void shouldThrowIfCartNotFound() {
            when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.clearCart(cartId, user)
            );

            verify(cartRepository).findById(cartId);
        }

        @Test
        void shouldThrowIfCartOwnedByAnotherUser() {
            existingCart.setUser(otherUser);
            when(cartRepository.findById(cartId)).thenReturn(Optional.of(existingCart));

            assertThrows(
                    AccessDeniedException.class,
                    () -> cartService.clearCart(cartId, user)
            );

            verify(cartRepository).findById(cartId);
        }
    }
}