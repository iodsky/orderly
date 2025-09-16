package com.iodsky.orderly.service.checkout;

import com.iodsky.orderly.model.Order;

import java.util.UUID;

public interface ICheckoutService {
    Order placeOrder(UUID cartId);
}
