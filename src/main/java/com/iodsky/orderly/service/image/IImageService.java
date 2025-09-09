package com.iodsky.orderly.service.image;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.iodsky.orderly.model.Image;

public interface IImageService {
  Image getImageById(UUID id);

  void deleteImageById(UUID id);

  List<Image> saveImage(List<MultipartFile> files, UUID productId);

  Image updateImage(MultipartFile file, UUID imageId);
}
