package com.iodsky.orderly.dto;

import com.iodsky.orderly.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;


import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class OrderDto {
    private UUID id;
    private Date createdAt;
    private BigDecimal totalAmount;
    private OrderStatus status;

    private Set<OrderItemDto> items;
}
