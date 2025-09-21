package com.iodsky.orderly.service.checkout;

import com.iodsky.orderly.model.Order;
import com.iodsky.orderly.model.User;

import java.util.UUID;

public interface ICheckoutService {
    Order placeOrder(UUID cartId, User user);
}
