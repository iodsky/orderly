package com.iodsky.orderly.service.product;

import com.iodsky.orderly.dto.product.ProductDto;
import com.iodsky.orderly.dto.product.ProductRequestDto;
import com.iodsky.orderly.model.Product;

import java.util.List;

public interface IProductService {
    ProductDto addProduct(ProductRequestDto productRequestDto);

    Product getProductEntity(Long id);

    ProductDto getProductDto(Long id);

    void deleteProductById(Long id);

    ProductDto updateProduct(Long productId, ProductRequestDto productRequestDto);

    List<ProductDto> getProducts(String name, String category, String brand);

    Long getProductsCountByBrandAndName(String brand, String name);

}
