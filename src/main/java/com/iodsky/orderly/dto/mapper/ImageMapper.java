package com.iodsky.orderly.dto.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.iodsky.orderly.dto.image.ImageDto;
import com.iodsky.orderly.model.Image;

@Component
public class ImageMapper {
  public static ImageDto toDto(Image image) {
    if (image == null) {
      return null;
    }

    String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/")
        .path(image.getId().toString())
        .toUriString();

    return ImageDto.builder()
        .id(image.getId())
        .fileName(image.getFileName())
        .fileType(image.getFileType())
        .productId(image.getProduct().getId())
        .imageUrl(imageUrl)
        .build();
  }
}
