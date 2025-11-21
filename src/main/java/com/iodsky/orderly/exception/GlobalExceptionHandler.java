package com.iodsky.orderly.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.iodsky.orderly.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class GlobalExceptionHandler {

  private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
    logger.warn("Resource not found: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse(
        LocalDateTime.now(), 404, ex.getMessage(), null);
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    logger.warn("HTTP message not readable: {}", ex.getMessage().split(":")[0]);

    ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 400, ex.getMessage().split(":")[0], null);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    List<ErrorResponse.FieldValidationError> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> new ErrorResponse.FieldValidationError(error.getField(), error.getDefaultMessage()))
        .toList();

    logger.warn("Validation failed for fields: {}",
        fieldErrors.stream()
            .map(ErrorResponse.FieldValidationError::getField)
            .toList());

    ErrorResponse response = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Validation Failed",
        fieldErrors);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
    logger.warn("Duplicate resource detected: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 409, ex.getMessage(), null);
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ResourceInUseException.class)
  public ResponseEntity<ErrorResponse> handleResourceInUserException(ResourceInUseException ex) {
    logger.warn("Resource in use: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 400, ex.getMessage(), null);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EmptyCartException.class)
  public ResponseEntity<ErrorResponse> handleEmptyCartException(EmptyCartException ex) {
    logger.warn("Empty cart exception: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 400, ex.getMessage(), null);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ProductOutOfStockException.class)
  public ResponseEntity<ErrorResponse> handleProductOutOfStockException(ProductOutOfStockException ex) {
    logger.warn("Product out of stock: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 400, ex.getMessage(), null);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    logger.warn("Access denied: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 403, "Forbidden", null);
    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(MissingServletRequestPartException ex) {
    logger.warn("Missing request part: {}", ex.getRequestPartName());

    ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 400, ex.getMessage(), null);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
    logger.warn("Authentication failed: Invalid credentials provided");

    ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 400, "Invalid username or password", null);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
    logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

    ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 500, "Internal Server Error", null);
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
