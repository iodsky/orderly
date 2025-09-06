package com.iodsky.orderly.exceptions;

public class ResourceInUseException extends RuntimeException {
  public ResourceInUseException(String message) {
    super(message);
  }

}
