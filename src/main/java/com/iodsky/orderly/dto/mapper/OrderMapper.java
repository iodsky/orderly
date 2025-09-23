package com.iodsky.orderly.dto.mapper;

import com.iodsky.orderly.dto.OrderDto;
import com.iodsky.orderly.dto.OrderItemDto;
import com.iodsky.orderly.model.Order;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDto.builder()
                .id(order.getId())
                .dateOrdered(order.getDateOrdered())
                .totalAmount(order.getTotalAmount())
                .status(order.getOrderStatus())
                .items(order.getItems()
                        .stream()
                        .map(item -> OrderItemDto.builder()
                                .id(order.getId())
                                .productId(item.getProduct().getId())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .productName(item.getProduct().getName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();

    }
}
