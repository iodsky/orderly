package com.iodsky.orderly.service.product;

import com.iodsky.orderly.dto.mapper.ProductMapper;
import com.iodsky.orderly.dto.product.ProductRequestDto;
import com.iodsky.orderly.exceptions.ResourceNotFoundException;
import com.iodsky.orderly.model.Category;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.CategoryRepository;
import com.iodsky.orderly.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product addProduct(ProductRequestDto productRequestDto) {
        Category category = categoryRepository.findByName(productRequestDto.getCategory())
                .orElseGet(() -> categoryRepository
                        .save(Category.builder().name(productRequestDto.getCategory()).build()));

        Product product = ProductMapper.toEntity(productRequestDto);
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override
    public Product getProductEntity(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id " + id));
    }

    @Override
    public Product getProductDto(UUID id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id " + id));
    }

    @Override
    public void deleteProductById(UUID id) {
        productRepository.findById(id).ifPresentOrElse(productRepository::delete, () -> {
            throw new ResourceNotFoundException("Product not found for id " + id);
        });
    }

    @Override
    public Product updateProduct(UUID id, ProductRequestDto productRequestDto) {
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

    @Override
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

    @Override
    public Long getProductsCountByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }
}
