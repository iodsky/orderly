package com.iodsky.orderly.service.order;

import com.iodsky.orderly.enums.OrderStatus;
import com.iodsky.orderly.model.Order;

import java.util.List;
import java.util.UUID;

public interface IOrderService {
    Order saveOrder(Order order);
    Order getOrder(UUID orderId);
    Order updateOrderStatus(UUID orderId, OrderStatus status);
    List<Order> getAllOrders();
}
