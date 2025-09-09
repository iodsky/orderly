package com.iodsky.orderly.service.product;

import com.iodsky.orderly.dto.product.ProductRequestDto;
import com.iodsky.orderly.model.Product;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    Product addProduct(ProductRequestDto productRequestDto);

    Product getProduct(UUID id);

    void deleteProductById(UUID id);

    Product updateProduct(UUID productId, ProductRequestDto productRequestDto);

    List<Product> getProducts(String name, String category, String brand);

    Long getProductsCountByBrandAndName(String brand, String name);

}
