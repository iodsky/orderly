package com.iodsky.orderly.controller;

import com.iodsky.orderly.dto.mapper.OrderMapper;
import com.iodsky.orderly.dto.order.OrderDto;
import com.iodsky.orderly.model.Order;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.service.checkout.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderMapper orderMapper;

    @PostMapping("{id}")
    public ResponseEntity<OrderDto> checkoutOrder(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        Order order = checkoutService.placeOrder(id, user);

        return new ResponseEntity<>(orderMapper.toDto(order), HttpStatus.CREATED);
    }
}
