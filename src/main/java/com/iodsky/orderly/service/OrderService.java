package com.iodsky.orderly.service;

import com.iodsky.orderly.enums.OrderStatus;
import com.iodsky.orderly.exception.EmptyCartException;
import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.*;
import com.iodsky.orderly.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder() {
        Cart cart = cartService.getUserCart();

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException(cart.getId());
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .totalAmount(cart.getTotalAmount())
                .orderStatus(OrderStatus.PROCESSING)
                .build();

        cart.getItems().forEach(item -> {

            Product product = productService.decreaseStock(item.getProduct().getId(), item.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .price(item.getUnitPrice())
                    .product(product)
                    .quantity(item.getQuantity())
                    .build();
            order.getItems().add(orderItem);
        });

        cart.getItems().clear();
        cartService.saveCart(cart);

        return orderRepository.save(order);
    }

    public Order getOrder(UUID orderId ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
        User user = userService.getAuthenticatedUser();

        boolean isOwner = order.getUser().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equals(user.getRole().getRole());

        if (!(isOwner || isAdmin)) {
            throw new AccessDeniedException("Only the owner or an administrator can access this order.");
        }

        return order;
    }

    public Order updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));

        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        User user = userService.getAuthenticatedUser();
        if (user.getRole().getRole().equals("ADMIN")) {
            return orderRepository.findAll();
        }

        return orderRepository.findAllByUserId(user.getId());
    }

}
