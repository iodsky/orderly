package com.iodsky.orderly.service;

import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Image;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private ProductService productService;

    @InjectMocks
    private ImageService imageService;

    private UUID randId;
    private Product product;
    private Image image1;
    private Image image2;
    private MultipartFile file1;
    private MultipartFile file2;

    @BeforeEach
    void setup() throws IOException {
        this.randId = UUID.randomUUID();

        this.product = new Product();
        
        this.file1 = new MockMultipartFile("file1", "file1.png", "image/png", "data1".getBytes());
        this.file2 = new MockMultipartFile("file2", "file2.jpg", "image/jpeg", "data2".getBytes());

        this.image1 = Image
                .builder()
                .product(product)
                .fileType(file1.getContentType())
                .fileName(file1.getOriginalFilename())
                .image(file1.getBytes())
                .build();

        this.image2 = Image
                .builder()
                .product(product)
                .fileType(file2.getContentType())
                .fileName(file2.getOriginalFilename())
                .image(file2.getBytes())
                .build();
    }

    @Nested
    @DisplayName("Get image tests")
    class getImageTests {

        @Test
        void shouldReturnImageIfFound() {
            when(imageRepository.findById(randId))
                    .thenReturn(Optional.of(image1));

            Image result = imageService.getImageById(randId);

            assertNotNull(result);
            assertEquals(image1, result);

            verify(imageRepository).findById(randId);
        }

        @Test
        void shouldThrowExceptionIfImageNotFound() {
            when(imageRepository.findById(randId))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> imageService.getImageById(randId)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));
            verify(imageRepository).findById(randId);
        }

    }

    @Nested
    @DisplayName("Delete image tests")
    class deleteImageTests {

        @Test
        void shouldDeleteImageIfFound() {
            when(imageRepository.findById(randId))
                    .thenReturn(Optional.of(image1));

            imageService.deleteImageById(randId);

            verify(imageRepository).findById(randId);
            verify(imageRepository).delete(image1);
        }

        @Test
        void shouldFailToDeleteIfImageNotFound() {
            when(imageRepository.findById(randId))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> imageService.deleteImageById(randId)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));

            verify(imageRepository).findById(randId);
            verify(imageRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Save image tests")
    class saveImageTest {

        @Test
        void shouldSaveImageIfProductFound() {
            when(productService.getProduct(randId))
                    .thenReturn(product);

            when(imageRepository.save(any(Image.class)))
                    .thenReturn(image1, image2);

            List<Image> result = imageService.saveImage(List.of(file1, file2), randId);

            assertEquals(2, result.size());
            assertEquals("file1.png", result.getFirst().getFileName());
            assertEquals("file2.jpg", result.get(1).getFileName());

            verify(productService).getProduct(randId);
            verify(imageRepository, times(2)).save(any(Image.class));
        }

        @Test
        void shouldFailToSaveImageIfProductNotFound() {
            when(productService.getProduct(randId))
                    .thenThrow(new ResourceNotFoundException("Product not found for id " + randId));

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> imageService.saveImage(Collections.emptyList(), randId)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));
            verify(productService).getProduct(randId);
            verify(imageRepository, never()).save(any(Image.class));
        }

    }

    @Nested
    @DisplayName("Update image tests")
    class updateImageTests {

        @Test
        void shouldUpdateImageIfFound() throws IOException {
            when(imageRepository.findById(randId))
                    .thenReturn(Optional.of(image1));
            when(imageRepository.save(any(Image.class)))
                    .then(inv -> inv.getArgument(0));

            Image result = imageService.updateImage(file2, randId);

            assertNotNull(result);
            assertEquals(file2.getOriginalFilename(), result.getFileName());
            assertEquals(file2.getContentType(), result.getFileType());
            assertEquals(file2.getBytes(), result.getImage());

            verify(imageRepository).findById(randId);
            verify(imageRepository).save(any(Image.class));

        }

        @Test
        void shouldFailToUpdateIfImageNotFound() {
            when(imageRepository.findById(randId))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> imageService.updateImage(any(MultipartFile.class), randId)
            );

            assertTrue(ex.getMessage().contains(randId.toString()));

            verify(imageRepository).findById(randId);
            verify(imageRepository, never()).save(any(Image.class));

        }

    }
}