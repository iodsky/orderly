package com.iodsky.orderly.service;

import com.iodsky.orderly.dto.mapper.ProductMapper;
import com.iodsky.orderly.exception.ProductOutOfStockException;
import com.iodsky.orderly.exception.ResourceInUseException;
import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Category;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.ProductRepository;
import com.iodsky.orderly.request.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    // Dependencies to mock
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService; // Service to test

    private Product existingProduct;
    private Category testCategory;
    private ProductRequest newProduct;
    private Category newCategory;
    private UUID randId;
    private ProductRequest updatedProduct;
    private Category updatedCategory;

    @BeforeEach
    void setup() {
        this.randId = UUID.randomUUID();

        this.testCategory = Category
                .builder()
                .name("Test Category")
                .build();

        this.existingProduct = Product
                .builder()
                .id(randId)
                .name("Test Product")
                .brand("Test Brand")
                .description("Test Description")
                .category(testCategory)
                .price(BigDecimal.valueOf(100))
                .stock(5)
                .build();

        this.newProduct = ProductRequest
                .builder()
                .name("New Product")
                .brand("New Brand")
                .description("New Description")
                .category("New Category")
                .price(BigDecimal.valueOf(100))
                .stock(7)
                .build();

        this.newCategory = Category.builder().name("New Category").build();

        this.updatedProduct = ProductRequest
                .builder()
                .name("Updated Product")
                .brand("Updated Brand")
                .description("Updated Description")
                .category("Updated Category")
                .price(BigDecimal.valueOf(100))
                .stock(6)
                .build();

        this.updatedCategory = Category.builder().name(updatedProduct.getCategory()).build();
    }

    @Nested
    @DisplayName("Create product tests")
    class createProductTests {

        @Test
        void shouldCreateProductWithExistingCategory() {
            // Given
            when(categoryService.getOrCreateCategory(newProduct.getCategory()))
                    .thenReturn(testCategory);
            when(productMapper.toEntity(newProduct))
                    .thenReturn(existingProduct);
            when(productRepository.save(any(Product.class)))
                    .thenReturn(existingProduct);

            // When
            final Product result = productService.addProduct(newProduct);

            // Then
            assertNotNull(result);
            assertEquals(testCategory, result.getCategory());

            verify(categoryService).getOrCreateCategory(anyString());
            verify(productMapper).toEntity(newProduct);
            verify(productRepository).save(existingProduct);

            verify(productRepository).save(argThat(p -> p.getCategory().getName().equals(testCategory.getName())));
        }

        @Test
        void shouldCreateProductWithNewCategory() {
            when(categoryService.getOrCreateCategory(anyString())).thenReturn(newCategory);
            when(productMapper.toEntity(any(ProductRequest.class))).thenReturn(existingProduct);
            when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

            final Product result = productService.addProduct(newProduct);

            assertNotNull(result);
            assertEquals(newCategory, result.getCategory());

            verify(categoryService).getOrCreateCategory(anyString());
            verify(productMapper).toEntity(newProduct);
            verify(productRepository).save(existingProduct);

            verify(productRepository).save(argThat(p -> p.getCategory().getName().equals(newCategory.getName())));
        }

    }

    @Nested
    @DisplayName("Find product tests")
    class findProductTest {
        @Test
        void shouldReturnProductIfFound() {
            when(productRepository.findById(randId)).thenReturn(Optional.of(existingProduct));

            final Product result = productService.getProduct(randId);

            assertNotNull(result);
            assertEquals(existingProduct, result);

            verify(productRepository).findById(randId);
        }

        @Test
        void shouldThrowExceptionIfProductNotFound() {
            when(productRepository.findById(randId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.getProduct(randId)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));
            verify(productRepository).findById(randId);
        }

    }

    @Nested
    @DisplayName("Delete product test")
    class deleteProductTest {
        @Test
        void shouldDeleteProductIfFound() {
            when(productRepository.findById(randId)).thenReturn(Optional.of(existingProduct));

            productService.deleteProductById(randId);

            verify(productRepository).findById(randId);
            verify(productRepository).delete(existingProduct);
        }

        @Test
        void shouldFailToDeleteIfProductNotFound() {
            when(productRepository.findById(randId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.deleteProductById(randId)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));
            verify(productRepository).findById(randId);
            verify(productRepository, never()).delete(any());
        }

        @Test
        void shouldFailToDeleteAndThrowExceptionIfProductInUse() {
            when(productRepository.findById(randId)).thenReturn(Optional.of(existingProduct));
            doThrow(DataIntegrityViolationException.class).when(productRepository).delete(existingProduct);

            ResourceInUseException ex = assertThrows(
                    ResourceInUseException.class,
                    () -> productService.deleteProductById(randId)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));
            verify(productRepository).findById(randId);
            verify(productRepository).delete(any());
        }

    }

    @Nested
    @DisplayName("Update product test")
    class updateProductTest {

        @Test
        void shouldUpdateProductIfFound() {
            when(productRepository.findById(randId)).thenReturn(Optional.of(existingProduct));
            when(categoryService.getOrCreateCategory(updatedProduct.getCategory())).thenReturn(updatedCategory);
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            Product updated = productService.updateProduct(randId, updatedProduct);

            assertNotNull(updated);
            assertEquals(updatedProduct.getName(), updated.getName());
            assertEquals(updatedProduct.getBrand(), updated.getBrand());
            assertEquals(updatedProduct.getDescription(), updated.getDescription());
            assertEquals(updatedProduct.getCategory(), updated.getCategory().getName());
            assertEquals(updatedProduct.getPrice(), updated.getPrice());
            assertEquals(updatedProduct.getStock(), updated.getStock());


            verify(productRepository).findById(randId);
            verify(categoryService).getOrCreateCategory(anyString());
            verify(productRepository).save(existingProduct);
        }

        @Test
        void shouldFailToUpdateIfProductNotFound() {
            when(productRepository.findById(randId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.updateProduct(randId, updatedProduct)
            );

            verify(productRepository).findById(randId);
            verify(categoryService, never()).getOrCreateCategory(anyString());
            verify(productRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("Get products test")
    class getProductsTest {

        @Test
        void shouldReturnProductsByCategoryAndBrand() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> expectedPage = new PageImpl<>(List.of(existingProduct), pageable, 1);

            when(productRepository.findByCategoryNameAndBrand("Test Category", "Test Brand", pageable))
                    .thenReturn(expectedPage);

            Page<Product> result = productService.getProducts(null, "Test Category", "Test Brand", 0, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            verify(productRepository).findByCategoryNameAndBrand("Test Category", "Test Brand", pageable);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        void shouldReturnProductsByBrandAndName() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> expectedPage = new PageImpl<>(List.of(existingProduct), pageable, 1);

            when(productRepository.findByBrandAndName("Test Brand", "Test Product", pageable))
                    .thenReturn(expectedPage);

            Page<Product> result = productService.getProducts("Test Product", null, "Test Brand", 0, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            verify(productRepository).findByBrandAndName("Test Brand", "Test Product", pageable);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        void shouldReturnProductsByCategory() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> expectedPage = new PageImpl<>(List.of(existingProduct), pageable, 1);

            when(productRepository.findByCategoryName("Test Category", pageable))
                    .thenReturn(expectedPage);

            Page<Product> result = productService.getProducts(null, "Test Category", null, 0, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            verify(productRepository).findByCategoryName("Test Category", pageable);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        void shouldReturnProductsByBrand() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> expectedPage = new PageImpl<>(List.of(existingProduct), pageable, 1);

            when(productRepository.findByBrand("Test Brand", pageable))
                    .thenReturn(expectedPage);

            Page<Product> result = productService.getProducts(null, null, "Test Brand", 0, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            verify(productRepository).findByBrand("Test Brand", pageable);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        void shouldReturnProductsByName() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> expectedPage = new PageImpl<>(List.of(existingProduct), pageable, 1);

            when(productRepository.findByName("Test Product", pageable))
                    .thenReturn(expectedPage);

            Page<Product> result = productService.getProducts("Test Product", null, null, 0, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            verify(productRepository).findByName("Test Product", pageable);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        void shouldReturnAllProductsIfNoFiltersProvided() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> expectedPage = new PageImpl<>(List.of(existingProduct), pageable, 1);

            when(productRepository.findAll(pageable))
                    .thenReturn(expectedPage);

            Page<Product> result = productService.getProducts(null, null, null, 0, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            verify(productRepository).findAll(pageable);
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Nested
    @DisplayName("Count products test")
    class getProductsCountByBrandAndNameTest {

        @Test
        void shouldReturnProductCountByBrandAndName() {
            when(productRepository.countByBrandAndName("Test Brand", "Test Product"))
                    .thenReturn(5L);

            Long count = productService.getProductsCountByBrandAndName("Test Brand", "Test Product");

            assertEquals(5L, count);
            verify(productRepository).countByBrandAndName("Test Brand", "Test Product");
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Nested
    @DisplayName("Decrease stock test")
    class decreaseStockTest {

        @Test
        void shouldDecreaseStockWhenSufficientQuantityAvailable() {
            when(productRepository.findById(randId)).thenReturn(Optional.of(existingProduct));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            Product updated = productService.decreaseStock(randId, 3);

            assertEquals(2, updated.getStock());
            verify(productRepository).findById(randId);
            verify(productRepository).save(existingProduct);
        }

        @Test
        void shouldThrowExceptionWhenStockIsInsufficient() {
            when(productRepository.findById(randId)).thenReturn(Optional.of(existingProduct));

            ProductOutOfStockException ex = assertThrows(
                    ProductOutOfStockException.class,
                    () -> productService.decreaseStock(randId, 6)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));
            verify(productRepository).findById(randId);
            verify(productRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenProductNotFound() {
            when(productRepository.findById(randId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.decreaseStock(randId, 5)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));
            verify(productRepository).findById(randId);
            verify(productRepository, never()).save(any());
        }
    }


}