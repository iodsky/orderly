package com.iodsky.orderly.dto.mapper;

import org.springframework.stereotype.Component;

import com.iodsky.orderly.dto.ImageDto;
import com.iodsky.orderly.model.Image;

@Component
public class ImageMapper {
  public ImageDto toDto(Image image) {
    if (image == null) {
      return null;
    }

    return ImageDto.builder()
        .id(image.getId())
        .fileName(image.getFileName())
        .fileType(image.getFileType())
        .productId(image.getProduct().getId())
        .build();
  }
}
