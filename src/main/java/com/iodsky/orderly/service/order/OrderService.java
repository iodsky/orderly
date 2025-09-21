package com.iodsky.orderly.service.order;

import com.iodsky.orderly.enums.OrderStatus;
import com.iodsky.orderly.exceptions.ResourceNotFoundException;
import com.iodsky.orderly.model.Order;
import com.iodsky.orderly.model.Role;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{

    private final OrderRepository orderRepository;

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order getOrder(UUID orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));

        boolean isOwner = order.getUser().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equals(user.getRole().getRole());

        if (!(isOwner || isAdmin)) {
            throw new AccessDeniedException("Only the owner or an administrator can access this order.");
        }

        return order;
    }

    @Override
    public Order updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));

        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders(User user) {
        if (user.getRole().getRole().equals("ADMIN")) {
            return orderRepository.findAll();
        }

        return orderRepository.findAllByUserId(user.getId());
    }
}
