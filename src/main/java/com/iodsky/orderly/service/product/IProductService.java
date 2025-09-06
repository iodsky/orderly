package com.iodsky.orderly.service.product;

import com.iodsky.orderly.dto.product.ProductRequestDto;
import com.iodsky.orderly.model.Product;

import java.util.List;

public interface IProductService {
    Product addProduct(ProductRequestDto productRequestDto);

    Product getProductEntity(Long id);

    Product getProductDto(Long id);

    void deleteProductById(Long id);

    Product updateProduct(Long productId, ProductRequestDto productRequestDto);

    List<Product> getProducts(String name, String category, String brand);

    Long getProductsCountByBrandAndName(String brand, String name);

}
