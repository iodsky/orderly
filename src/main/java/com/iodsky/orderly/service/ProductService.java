package com.iodsky.orderly.service;

import com.iodsky.orderly.dto.mapper.ProductMapper;
import com.iodsky.orderly.request.ProductRequest;
import com.iodsky.orderly.exception.ProductOutOfStockException;
import com.iodsky.orderly.exception.ResourceInUseException;
import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Category;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;

    public Product addProduct(ProductRequest request) {
        Category category = categoryService.getOrCreateCategory(request.getCategory());
        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        return productRepository.save(product);
    }

    public Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id " + id));
    }

    public void deleteProductById(UUID id) {
        try {
            productRepository.findById(id).ifPresentOrElse(productRepository::delete, () -> {
                throw new ResourceNotFoundException("Product not found for id " + id);
            });
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceInUseException("Cannot delete product " + id + " because it has associated order/s");
        }
    }

    public Product updateProduct(UUID id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id " + id));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());

        Category category = categoryService.getOrCreateCategory(request.getCategory());

        existingProduct.setCategory(category);

        return productRepository.save(existingProduct);
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    public List<Product> getProducts(String name, String category, String brand) {
        name = normalize(name);
        category = normalize(category);
        brand = normalize(brand);

        if (category != null && brand != null) {
            return productRepository.findByCategoryNameAndBrand(category, brand);
        } else if (brand != null && name != null) {
            return productRepository.findByBrandAndName(brand, name);
        } else if (category != null) {
            return productRepository.findByCategoryName(category);
        } else if (brand != null) {
            return productRepository.findByBrand(brand);
        } else if (name != null) {
            return productRepository.findByName(name);
        } else {
            return productRepository.findAll();
        }

    }

    public Long getProductsCountByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    public Product decreaseStock(UUID id, int quantity) {
        Product product = getProduct(id);

        if (product.getStock() < quantity) {
            throw new ProductOutOfStockException(product.getId());
        }

        product.setStock(product.getStock() - quantity);
        return productRepository.save(product);
    }
}
