package com.iodsky.orderly.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iodsky.orderly.exception.ResourceNotFoundException;
import com.iodsky.orderly.model.Image;
import com.iodsky.orderly.model.Product;
import com.iodsky.orderly.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageRepository imageRepository;
  private final ProductService productService;
  private final S3Service s3Service;
  private final String FOLDER = "product-images";

  public record ImageStreamData(String fileType, String fileName, InputStreamResource resource) { }

  public ImageStreamData getImageStream(UUID imageId) {
    Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new ResourceNotFoundException("Image not found for id " + imageId));

    ResponseInputStream<GetObjectResponse> s3Stream = s3Service.getObjectStream(FOLDER, image.getFileName());
    InputStreamResource resource = new InputStreamResource(s3Stream);

    return new ImageStreamData(image.getFileType(), image.getFileName(), resource);
  }

  public void deleteImageById(UUID id) {
    imageRepository.findById(id).ifPresentOrElse(image -> {
      imageRepository.delete(image);
      s3Service.deleteObject(FOLDER, image.getFileName());
    }, () -> {
      throw new ResourceNotFoundException("Image not found for id " + id);
    });
  }

  public List<Image> saveImage(List<MultipartFile> files, UUID productId) {
    Product product = productService.getProduct(productId);

    List<Image> savedImages = new ArrayList<>();
      for (MultipartFile file : files) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Service.putObject(file, FOLDER, fileName);

        Image image = new Image();
        image.setFileType(file.getContentType());
        image.setFileName(fileName);
        image.setProduct(product);

        Image savedImage = imageRepository.save(image);
        savedImages.add(savedImage);
      }

      return savedImages;
}

  public Image updateImage(MultipartFile file, UUID imageId) {
    Image existingImage = imageRepository.findById(imageId)
        .orElseThrow(() -> new ResourceNotFoundException("Image not found for id " + imageId));

      String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
      s3Service.putObject(file, FOLDER, fileName);

      existingImage.setFileType(file.getContentType());
      existingImage.setFileName(fileName);

      return imageRepository.save(existingImage);
  }

}
