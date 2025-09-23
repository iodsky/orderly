package com.iodsky.orderly.request;

import com.iodsky.orderly.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

  @NotNull(message = "Order status cannot be blank")
  private OrderStatus status;
}
