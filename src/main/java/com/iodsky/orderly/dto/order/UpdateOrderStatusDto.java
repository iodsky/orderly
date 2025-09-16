package com.iodsky.orderly.dto.order;

import com.iodsky.orderly.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusDto {

  @NotNull(message = "Order status cannot be blank")
  private OrderStatus status;
}
