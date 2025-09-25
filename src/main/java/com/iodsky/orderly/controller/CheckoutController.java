package com.iodsky.orderly.controller;

import com.iodsky.orderly.dto.mapper.OrderMapper;
import com.iodsky.orderly.dto.OrderDto;
import com.iodsky.orderly.model.Order;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderMapper orderMapper;

    @Operation(
            summary = "Creates a new order from the authenticated user's cart."
    )
    @PostMapping()
    public ResponseEntity<OrderDto> checkoutOrder(@AuthenticationPrincipal User user) {
        Order order = checkoutService.placeOrder(user);

        return new ResponseEntity<>(orderMapper.toDto(order), HttpStatus.CREATED);
    }
}
