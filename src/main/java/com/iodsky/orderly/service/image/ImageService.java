package com.iodsky.orderly.service.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iodsky.orderly.exceptions.ResourceNotFoundException;
import com.iodsky.orderly.model.Image;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.ImageRepository;
import com.iodsky.orderly.service.product.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

  private final ImageRepository imageRepository;
  private final ProductService productService;

  @Override
  public Image getImageById(Long id) {
    return imageRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Image not found for id " + id));
  }

  @Override
  public void deleteImageById(Long id) {
    imageRepository.findById(id).ifPresentOrElse(imageRepository::delete, () -> {
      throw new ResourceNotFoundException("Image not found for id " + id);
    });
  }

  @Override
  public List<Image> saveImage(List<MultipartFile> files, Long productId) {
    Product product = productService.getProductEntity(productId);

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

  @Override
  public Image updateImage(MultipartFile file, Long imageId) {
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
