package com.iodsky.orderly.service.order;

import com.iodsky.orderly.enums.OrderStatus;
import com.iodsky.orderly.exceptions.ResourceNotFoundException;
import com.iodsky.orderly.model.Order;
import com.iodsky.orderly.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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
    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found id " + orderId));
    }

    @Override
    public Order updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = getOrder(orderId);
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
