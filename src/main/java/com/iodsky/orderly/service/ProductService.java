package com.iodsky.orderly.service;

import com.iodsky.orderly.dto.mapper.ProductMapper;
import com.iodsky.orderly.request.ProductRequest;
import com.iodsky.orderly.exception.ProductOutOfStockException;
import com.iodsky.orderly.exception.ResourceInUseException;
import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Category;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public Product addProduct(ProductRequest productRequestDto) {
        Category category = categoryRepository.findByName(productRequestDto.getCategory())
                .orElseGet(() -> categoryRepository
                        .save(Category.builder().name(productRequestDto.getCategory()).build()));

        Product product = productMapper.toEntity(productRequestDto);
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

    public Product updateProduct(UUID id, ProductRequest productRequestDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id " + id));

        existingProduct.setName(productRequestDto.getName());
        existingProduct.setDescription(productRequestDto.getDescription());
        existingProduct.setBrand(productRequestDto.getBrand());
        existingProduct.setPrice(productRequestDto.getPrice());
        existingProduct.setStock(productRequestDto.getStock());

        Category category = categoryRepository.findByName(productRequestDto.getCategory())
                .orElseGet(() -> categoryRepository
                        .save(Category.builder().name(productRequestDto.getCategory()).build()));

        existingProduct.setCategory(category);

        return productRepository.save(existingProduct);
    }

    public List<Product> getProducts(String name, String category, String brand) {
        List<Product> products;

        if (category != null && brand != null) {
            products = productRepository.findByCategoryNameAndBrand(category, brand);
        } else if (brand != null && name != null) {
            products = productRepository.findByBrandAndName(brand, name);
        } else if (category != null) {
            products = productRepository.findByCategoryName(category);
        } else if (brand != null) {
            products = productRepository.findByBrand(brand);
        } else if (name != null) {
            products = productRepository.findByName(name);
        } else {
            products = productRepository.findAll();
        }

        return products;
    }

    public Long getProductsCountByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    public Product decreaseStock(UUID id, int quanity) {
        Product product = getProduct(id);

        if (product.getStock() < quanity) {
            throw new ProductOutOfStockException(product.getId());
        }

        product.setStock(product.getStock() - quanity);
        return productRepository.save(product);
    }
}
