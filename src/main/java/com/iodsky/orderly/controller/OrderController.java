package com.iodsky.orderly.controller;

import com.iodsky.orderly.dto.mapper.OrderMapper;
import com.iodsky.orderly.dto.OrderDto;
import com.iodsky.orderly.request.UpdateOrderStatusRequest;
import com.iodsky.orderly.model.Order;
import com.iodsky.orderly.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;


    @PostMapping
    public ResponseEntity<OrderDto> createOrder() {
        Order order = orderService.createOrder();
        return new ResponseEntity<>(orderMapper.toDto(order), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Fetches all orders for the authenticated user."
    )
    @GetMapping()
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders().stream().map(orderMapper::toDto).toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Fetches an order by it's ID for the authenticated user."
    )
    @GetMapping("{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    @Operation(
            summary = "Updates the status of an order by ID. Only admins can perform this action."
    )
    @PatchMapping("{id}")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest dto) {
        Order order = orderService.updateOrderStatus(id, dto.getStatus());
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

}
