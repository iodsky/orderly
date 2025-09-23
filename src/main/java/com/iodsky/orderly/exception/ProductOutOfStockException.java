package com.iodsky.orderly.exception;

import java.util.UUID;

public class ProductOutOfStockException extends RuntimeException {
  public ProductOutOfStockException(UUID id) {
    super("Product " + id + " is out of stock");
  }
}
