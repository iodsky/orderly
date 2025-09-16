package com.iodsky.orderly.dto.order;

import com.iodsky.orderly.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class OrderDto {
    private UUID id;
    private LocalDateTime dateOrdered;
    private BigDecimal totalAmount;
    private OrderStatus status;

    private Set<OrderItemDto> items;
}
