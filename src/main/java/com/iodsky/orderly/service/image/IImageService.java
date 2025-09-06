package com.iodsky.orderly.service.image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.iodsky.orderly.model.Image;

public interface IImageService {
  Image getImageById(Long id);

  void deleteImageById(Long id);

  List<Image> saveImage(List<MultipartFile> files, Long productId);

  Image updateImage(MultipartFile file, Long imageId);
}
