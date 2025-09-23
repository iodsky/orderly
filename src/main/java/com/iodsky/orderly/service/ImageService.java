package com.iodsky.orderly.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Image;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageRepository imageRepository;
  private final ProductService productService;

  public Image getImageById(UUID id) {
    return imageRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Image not found for id " + id));
  }

  public void deleteImageById(UUID id) {
    imageRepository.findById(id).ifPresentOrElse(imageRepository::delete, () -> {
      throw new ResourceNotFoundException("Image not found for id " + id);
    });
  }

  public List<Image> saveImage(List<MultipartFile> files, UUID productId) {
    Product product = productService.getProduct(productId);

    List<Image> savedImages = new ArrayList<>();
    try {
      for (MultipartFile file : files) {
        Image image = new Image();
        image.setFileType(file.getContentType());
        image.setFileName(file.getOriginalFilename());
        image.setImage(file.getBytes());
        image.setProduct(product);

        Image savedImage = imageRepository.save(image);
        savedImages.add(savedImage);
      }

      return savedImages;
    } catch (IOException e) {
      throw new RuntimeException("An error has occured while saving image " + e.getMessage());
    }
  }

  public Image updateImage(MultipartFile file, UUID imageId) {
    Image existingImage = imageRepository.findById(imageId)
        .orElseThrow(() -> new ResourceNotFoundException("Image not found for id " + imageId));

    try {
      existingImage.setFileType(file.getContentType());
      existingImage.setFileName(file.getOriginalFilename());
      existingImage.setImage(file.getBytes());

      return imageRepository.save(existingImage);
    } catch (IOException e) {
      throw new RuntimeException("An error has occured while updating image " + e.getMessage());
    }
  }
}
