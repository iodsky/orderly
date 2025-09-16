package com.iodsky.orderly.exceptions;

import java.util.UUID;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException(UUID cartId) {
        super("Cart with Id " + cartId + " is empty");
    }
}
