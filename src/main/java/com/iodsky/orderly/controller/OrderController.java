package com.iodsky.orderly.controller;

import com.iodsky.orderly.dto.mapper.OrderMapper;
import com.iodsky.orderly.dto.order.OrderDto;
import com.iodsky.orderly.dto.order.UpdateOrderStatusDto;
import com.iodsky.orderly.model.Order;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping()
    public ResponseEntity<List<OrderDto>> getAllOrders(@AuthenticationPrincipal User user) {
        List<OrderDto> orders = orderService.getAllOrders(user).stream().map(orderMapper::toDto).toList();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        Order order = orderService.getOrder(id, user);
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable UUID id, @RequestBody UpdateOrderStatusDto dto) {
        Order order = orderService.updateOrderStatus(id, dto.getStatus());
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

}
