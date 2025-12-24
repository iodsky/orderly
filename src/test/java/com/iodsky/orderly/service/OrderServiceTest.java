package com.iodsky.orderly.service;

import com.iodsky.orderly.enums.OrderStatus;
import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.*;
import com.iodsky.orderly.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private User normalUser;
    private User adminUser;
    private Order order;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        normalUser = User.builder()
                .id(UUID.randomUUID())
                .username("normalUser")
                .role(Role.builder().role("USER").build())
                .build();

        adminUser = User.builder()
                .id(UUID.randomUUID())
                .username("adminUser")
                .role(Role.builder().role("ADMIN").build())
                .build();

        order = Order.builder()
                .id(orderId)
                .user(normalUser)
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    @Nested
    @DisplayName("saveOrder tests")
    class SaveOrderTests {
        @Test
        void shouldSaveOrder() {
            Cart cart = Cart.builder()
                    .id(UUID.randomUUID())
                    .user(normalUser)
                    .items(new HashSet<>())
                    .build();

            // Add a cart item so the cart is not empty
            CartItem cartItem = new CartItem();
            cartItem.setProduct(Product.builder().id(UUID.randomUUID()).build());
            cartItem.setQuantity(1);
            cartItem.setUnitPrice(BigDecimal.TEN);
            cart.getItems().add(cartItem);

            when(cartService.getUserCart()).thenReturn(cart);
            when(productService.decreaseStock(any(UUID.class), anyInt())).thenReturn(cartItem.getProduct());
            when(cartService.saveCart(any(Cart.class))).thenReturn(cart);
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderService.createOrder();

            assertNotNull(result);
            assertEquals(normalUser, result.getUser());
            assertEquals(OrderStatus.PENDING, result.getOrderStatus());
            verify(cartService).getUserCart();
            verify(cartService).saveCart(any(Cart.class));
            verify(orderRepository).save(any(Order.class));
        }
    }

    @Nested
    @DisplayName("getOrder tests")
    class GetOrderTests {
        @Test
        void shouldReturnOrderIfUserIsOwner() {
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(userService.getAuthenticatedUser()).thenReturn(normalUser);

            Order result = orderService.getOrder(orderId);

            assertEquals(order, result);
            verify(orderRepository).findById(orderId);
            verify(userService).getAuthenticatedUser();
        }

        @Test
        void shouldReturnOrderIfUserIsAdmin() {
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(userService.getAuthenticatedUser()).thenReturn(adminUser);

            Order result = orderService.getOrder(orderId);

            assertEquals(order, result);
            verify(orderRepository).findById(orderId);
            verify(userService).getAuthenticatedUser();
        }

        @Test
        void shouldThrowIfOrderNotFound() {
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.getOrder(orderId));

            verify(orderRepository).findById(orderId);
        }

        @Test
        void shouldThrowIfUserNotOwnerOrAdmin() {
            User otherUser = User.builder()
                    .id(UUID.randomUUID())
                    .username("otherUser")
                    .role(Role.builder().role("USER").build())
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(userService.getAuthenticatedUser()).thenReturn(otherUser);

            assertThrows(AccessDeniedException.class,
                    () -> orderService.getOrder(orderId));

            verify(orderRepository).findById(orderId);
            verify(userService).getAuthenticatedUser();
        }
    }

    @Nested
    @DisplayName("updateOrderStatus tests")
    class UpdateOrderStatusTests {
        @Test
        void shouldUpdateStatusIfAdmin() {
            when(userService.getAuthenticatedUser()).thenReturn(adminUser);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED);

            assertNotNull(result);
            assertEquals(OrderStatus.SHIPPED, result.getOrderStatus());

            verify(userService).getAuthenticatedUser();
            verify(orderRepository).findById(orderId);
            verify(orderRepository).save(order);
        }

        @Test
        void shouldThrowIfOrderNotFound() {
            when(userService.getAuthenticatedUser()).thenReturn(adminUser);
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED));

            verify(userService).getAuthenticatedUser();
            verify(orderRepository).findById(orderId);
        }

        @Test
        void shouldAllowOwnerToCancelPendingOrder() {
            when(userService.getAuthenticatedUser()).thenReturn(normalUser);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);

            assertNotNull(result);
            assertEquals(OrderStatus.CANCELLED, result.getOrderStatus());

            verify(userService).getAuthenticatedUser();
            verify(orderRepository).findById(orderId);
            verify(orderRepository).save(order);
        }

        @Test
        void shouldThrowIfNonOwnerTriesToUpdateOrder() {
            User otherUser = User.builder()
                    .id(UUID.randomUUID())
                    .username("otherUser")
                    .role(Role.builder().role("USER").build())
                    .build();

            when(userService.getAuthenticatedUser()).thenReturn(otherUser);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            assertThrows(Exception.class,
                    () -> orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED));

            verify(userService).getAuthenticatedUser();
            verify(orderRepository).findById(orderId);
        }

        @Test
        void shouldThrowIfOwnerTriesToSetNonCancelledStatus() {
            when(userService.getAuthenticatedUser()).thenReturn(normalUser);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            assertThrows(Exception.class,
                    () -> orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED));

            verify(userService).getAuthenticatedUser();
            verify(orderRepository).findById(orderId);
        }

        @Test
        void shouldThrowIfOwnerTriesToCancelNonPendingOrder() {
            order.setOrderStatus(OrderStatus.SHIPPED);

            when(userService.getAuthenticatedUser()).thenReturn(normalUser);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            assertThrows(Exception.class,
                    () -> orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED));

            verify(userService).getAuthenticatedUser();
            verify(orderRepository).findById(orderId);
        }
    }

    @Nested
    @DisplayName("getAllOrders tests")
    class GetAllOrdersTests {
        @Test
        void shouldReturnAllOrdersIfAdmin() {
            when(userService.getAuthenticatedUser()).thenReturn(adminUser);
            when(orderRepository.findAll()).thenReturn(List.of(order));

            List<Order> result = orderService.getAllOrders();

            assertEquals(1, result.size());
            assertEquals(order, result.get(0));
            verify(userService).getAuthenticatedUser();
            verify(orderRepository).findAll();
        }

        @Test
        void shouldReturnUserOrdersIfNotAdmin() {
            when(userService.getAuthenticatedUser()).thenReturn(normalUser);
            when(orderRepository.findAllByUserId(normalUser.getId())).thenReturn(List.of(order));

            List<Order> result = orderService.getAllOrders();

            assertEquals(1, result.size());
            assertEquals(order, result.get(0));
            verify(userService).getAuthenticatedUser();
            verify(orderRepository).findAllByUserId(normalUser.getId());
        }
    }
}