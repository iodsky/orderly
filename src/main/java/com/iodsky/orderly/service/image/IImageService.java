package com.iodsky.orderly.service.image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.iodsky.orderly.dto.image.ImageDto;

public interface IImageService {
  ImageDto getImageById(Long id);

  void deleteImageById(Long id);

  List<ImageDto> saveImage(List<MultipartFile> files, Long productId);

  ImageDto updateImage(MultipartFile file, Long imageId);
}
