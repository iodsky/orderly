package com.iodsky.orderly.service;

import com.iodsky.orderly.enums.OrderStatus;
import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Order;
import com.iodsky.orderly.model.Role;
import com.iodsky.orderly.model.User;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

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
                .orderStatus(OrderStatus.PROCESSING)
                .build();
    }

    @Nested
    @DisplayName("saveOrder tests")
    class SaveOrderTests {
        @Test
        void shouldSaveOrder() {
            when(orderRepository.save(order)).thenReturn(order);

            Order result = orderService.saveOrder(order);

            assertNotNull(result);
            assertEquals(order, result);
            verify(orderRepository).save(order);
        }
    }

    @Nested
    @DisplayName("getOrder tests")
    class GetOrderTests {
        @Test
        void shouldReturnOrderIfUserIsOwner() {
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            Order result = orderService.getOrder(orderId, normalUser);

            assertEquals(order, result);
            verify(orderRepository).findById(orderId);
        }

        @Test
        void shouldReturnOrderIfUserIsAdmin() {
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            Order result = orderService.getOrder(orderId, adminUser);

            assertEquals(order, result);
            verify(orderRepository).findById(orderId);
        }

        @Test
        void shouldThrowIfOrderNotFound() {
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.getOrder(orderId, normalUser));

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

            assertThrows(AccessDeniedException.class,
                    () -> orderService.getOrder(orderId, otherUser));

            verify(orderRepository).findById(orderId);
        }
    }

    @Nested
    @DisplayName("updateOrderStatus tests")
    class UpdateOrderStatusTests {
        @Test
        void shouldUpdateStatus() {
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED);

            assertNotNull(result);
            assertEquals(OrderStatus.SHIPPED, result.getOrderStatus());

            verify(orderRepository).findById(orderId);
            verify(orderRepository).save(order);
        }

        @Test
        void shouldThrowIfOrderNotFound() {
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED));

            verify(orderRepository).findById(orderId);
        }
    }

    @Nested
    @DisplayName("getAllOrders tests")
    class GetAllOrdersTests {
        @Test
        void shouldReturnAllOrdersIfAdmin() {
            when(orderRepository.findAll()).thenReturn(List.of(order));

            List<Order> result = orderService.getAllOrders(adminUser);

            assertEquals(1, result.size());
            assertEquals(order, result.get(0));
            verify(orderRepository).findAll();
        }

        @Test
        void shouldReturnUserOrdersIfNotAdmin() {
            when(orderRepository.findAllByUserId(normalUser.getId())).thenReturn(List.of(order));

            List<Order> result = orderService.getAllOrders(normalUser);

            assertEquals(1, result.size());
            assertEquals(order, result.get(0));
            verify(orderRepository).findAllByUserId(normalUser.getId());
        }
    }
}