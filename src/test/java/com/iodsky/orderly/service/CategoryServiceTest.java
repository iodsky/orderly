package com.iodsky.orderly.service;

import com.iodsky.orderly.dto.CategoryDto;
import com.iodsky.orderly.dto.mapper.CategoryMapper;
import com.iodsky.orderly.exception.DuplicateResourceException;
import com.iodsky.orderly.exception.ResourceInUseException;
import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Category;
import com.iodsky.orderly.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category existingCategory;
    private CategoryDto newCategory;
    private CategoryDto updatedCategory;
    private UUID randId;

    @BeforeEach
    void setUp() {
        this.randId = UUID.randomUUID();

        this.existingCategory = Category
                .builder()
                .id(randId)
                .name("Test Category")
                .build();

        this.updatedCategory = CategoryDto
                .builder()
                .id(randId)
                .name("Updated Category")
                .build();

        this.newCategory = CategoryDto
                .builder()
                .name("New Category")
                .build();
    }

    @Nested
    @DisplayName("Create category test")
    class addCategoryTest {

        @Test
        void shouldCreateCategoryIfNameNotFound() {
            when(categoryMapper.toEntity(any()))
                    .thenReturn(existingCategory);

            when(categoryRepository.save(any()))
                    .thenReturn(existingCategory);

            Category result = categoryService.addCategory(newCategory);

            assertNotNull(result);
            assertEquals(existingCategory.getName(), result.getName());

            verify(categoryMapper).toEntity(any());
            verify(categoryRepository).save(any());
        }

        @Test
        void shouldFailToCreateCategoryIfNameIsUsed() {
            when(categoryMapper.toEntity(any()))
                    .thenReturn(existingCategory);
            doThrow(DataIntegrityViolationException.class)
                    .when(categoryRepository).save(any());

            DuplicateResourceException ex = assertThrows(
                    DuplicateResourceException.class,
                    () -> categoryService.addCategory(newCategory)
            );

            assertTrue(ex.getMessage().contains(newCategory.getName()));

            verify(categoryMapper).toEntity(any());
            verify(categoryRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Find category test")
    class findCategoryTest {

        @Test
        void shouldReturnCategoryIfFound() {
            when(categoryRepository.findById(randId))
                    .thenReturn(Optional.of(existingCategory));

            Category result = categoryService.getCategoryById(randId);

            assertNotNull(result);
            assertEquals(existingCategory, result);

            verify(categoryRepository).findById(randId);
        }

        @Test
        void shouldThrowExceptionIfCategoryNotFound() {
            when(categoryRepository.findById(randId))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.getCategoryById(randId)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));
            verify(categoryRepository).findById(randId);
        }

    }

    @Nested
    @DisplayName("Get or create category test")
    class getOrCreateCategoryTest {

        @Test
        void shouldReturnCategoryIfFound() {
            when(categoryRepository.findByName(anyString()))
                    .thenReturn(Optional.of(existingCategory));

            Category result = categoryService.getOrCreateCategory(anyString());

            assertNotNull(result);
            assertEquals(existingCategory, result);

            verify(categoryRepository).findByName(anyString());
            verify(categoryRepository, never()).save(any());
        }

        @Test
        void shouldCreateCategoryIfNotFound() {
            when(categoryRepository.findByName(anyString()))
                    .thenReturn(Optional.empty());
            when(categoryRepository.save(any()))
                    .thenReturn(existingCategory);

            Category result = categoryService.getOrCreateCategory(anyString());

            assertNotNull(result);
            assertEquals(existingCategory, result);

            verify(categoryRepository).findByName(anyString());
            verify(categoryRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Update category test")
    class updateCategoryTest {

        @Test
        void shouldUpdateCategoryIfFound() {
            when(categoryRepository.findById(randId))
                .thenReturn(Optional.of(existingCategory));
            when(categoryRepository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            Category updated = categoryService.updateCategory(randId, updatedCategory);

            assertNotNull(updated);
            assertEquals(updatedCategory.getId(), updated.getId());
            assertEquals(updatedCategory.getName(), updated.getName());

            verify(categoryRepository).findById(randId);
            verify(categoryRepository).save(any());
        }

        @Test
        void shouldFailToUpdateCategoryIfNotFound() {
            when(categoryRepository.findById(randId))
                .thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.updateCategory(randId, updatedCategory)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));

            verify(categoryRepository).findById(randId);
            verify(categoryRepository, never()).save(any());
        }

        @Test
        void shouldFailToUpdateCategoryAndThrowExceptionIfNameInUse() {
            when(categoryRepository.findById(randId))
                    .thenReturn(Optional.of(existingCategory));
            doThrow(DataIntegrityViolationException.class)
                    .when(categoryRepository).save(any());

            DuplicateResourceException rx = assertThrows(
                    DuplicateResourceException.class,
                    () -> categoryService.updateCategory(randId, updatedCategory)
            );

            assertTrue(rx.getMessage().contains(updatedCategory.getName()));

            verify(categoryRepository).findById(randId);
            verify(categoryRepository).save(any());
        }
    }
    
    @Nested
    @DisplayName("Get categories test")
    class getCategoriesTest {
        
        @Test
        void shouldReturnAllCategories() {
            when(categoryRepository.findAll())
                    .thenReturn(List.of(existingCategory));

            List<Category> result = categoryService.getAllCategories();

            assertEquals(1, result.size());
            verify(categoryRepository).findAll();
        }

        @Test
        void shouldReturnEmptyListIfNoCategoriesFound() {
            when(categoryRepository.findAll())
                    .thenReturn(Collections.emptyList());

            List<Category> result = categoryService.getAllCategories();

            assertTrue(result.isEmpty());
            verify(categoryRepository).findAll();
        }
        
    }

    @Nested
    @DisplayName("Delete category test")
    class deleteCategoryTest {

        @Test
        void shouldDeleteCategoryIfFound() {
            when(categoryRepository.findById(randId))
                    .thenReturn(Optional.of(existingCategory));

            categoryService.deleteCategoryById(randId);

            verify(categoryRepository).findById(randId);
            verify(categoryRepository).delete(existingCategory);
        }

        @Test
        void shouldFailToDeleteCategoryAndThrowExceptionIfNotFound() {
            when(categoryRepository.findById(randId))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.deleteCategoryById(randId)
            );

            assertTrue(ex.getMessage().contains("not found"));
            verify(categoryRepository).findById(randId);
            verify(categoryRepository, never()).delete(existingCategory);
        }

        @Test
        void shouldFailToDeleteCategoryAndThrowExceptionIfCategoryInUse() {
            when(categoryRepository.findById(randId))
                    .thenReturn(Optional.of(existingCategory));
            doThrow(DataIntegrityViolationException.class)
                    .when(categoryRepository).delete(existingCategory);

            ResourceInUseException ex = assertThrows(
                    ResourceInUseException.class,
                    () -> categoryService.deleteCategoryById(randId)
            );

            assertTrue(ex.getMessage().contains("associated"));
            verify(categoryRepository).findById(randId);
            verify(categoryRepository).delete(existingCategory);
        }
    }
}