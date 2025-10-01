package com.iodsky.orderly.service;

import com.iodsky.orderly.enums.OrderStatus;
import com.iodsky.orderly.exception.EmptyCartException;
import com.iodsky.orderly.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CheckoutService checkoutService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .build();

        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .price(BigDecimal.valueOf(50))
                .stock(10)
                .build();

        cartItem = CartItem.builder()
                .id(UUID.randomUUID())
                .product(product)
                .cart(cart)
                .unitPrice(product.getPrice())
                .quantity(2)
                .build();

        cart = Cart.builder()
                .id(UUID.randomUUID())
                .user(user)
                .items(new HashSet<>(Collections.singleton(cartItem)))
                .build();
    }

    @Nested
    @DisplayName("placeOrder tests")
    class PlaceOrderTests {

        @Test
        void shouldThrowExceptionIfCartIsEmpty() {
            cart.getItems().clear();
            when(cartService.getCartByUser(user)).thenReturn(cart);

            assertThrows(EmptyCartException.class,
                    () -> checkoutService.placeOrder(user));

            verify(cartService).getCartByUser(user);
            verifyNoInteractions(productService, orderService);
        }

        @Test
        void shouldPlaceOrder() {
            when(cartService.getCartByUser(user)).thenReturn(cart);
            when(productService.decreaseStock(product.getId(), cartItem.getQuantity()))
                    .thenReturn(product);
            when(cartService.saveCart(cart)).thenReturn(cart);
            when(orderService.saveOrder(any(Order.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Order result = checkoutService.placeOrder(user);

            assertNotNull(result);
            assertEquals(user, result.getUser());
            assertEquals(OrderStatus.PROCESSING, result.getOrderStatus());
            assertEquals(BigDecimal.valueOf(100), result.getTotalAmount());
            assertEquals(1, result.getItems().size());

            OrderItem orderItem = result.getItems().iterator().next();
            assertEquals(product, orderItem.getProduct());
            assertEquals(2, orderItem.getQuantity());
            assertEquals(product.getPrice(), orderItem.getPrice());

            assertTrue(cart.getItems().isEmpty());

            verify(cartService).getCartByUser(user);
            verify(productService).decreaseStock(product.getId(), cartItem.getQuantity());
            verify(cartService).saveCart(cart);
            verify(orderService).saveOrder(any(Order.class));
        }
    }
}