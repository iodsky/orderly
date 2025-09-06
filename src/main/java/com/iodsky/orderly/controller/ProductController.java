package com.iodsky.orderly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iodsky.orderly.dto.mapper.ProductMapper;
import com.iodsky.orderly.dto.product.ProductDto;
import com.iodsky.orderly.dto.product.ProductRequestDto;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.service.product.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;
  private final ProductMapper productMapper;

  @PostMapping
  public ResponseEntity<ProductDto> createProduct(@Valid() @RequestBody() ProductRequestDto productRequestDto) {
    Product product = productService.addProduct(productRequestDto);
    return new ResponseEntity<>(productMapper.toDto(product), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<ProductDto>> getProducts(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) String name) {

    List<ProductDto> products = productService.getProducts(name, category, brand).stream()
        .map(productMapper::toDto).toList();
    return ResponseEntity.ok(products);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
    Product product = productService.getProductDto(id);
    return ResponseEntity.ok(productMapper.toDto(product));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID id,
      @Valid() @RequestBody() ProductRequestDto productRequestDto) {
    Product product = productService.updateProduct(id, productRequestDto);
    return ResponseEntity.ok(productMapper.toDto(product));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
    productService.deleteProductById(id);
    return ResponseEntity.ok("Product deleted successfully");
  }

}
