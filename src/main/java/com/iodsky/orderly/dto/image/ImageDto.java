package com.iodsky.orderly.dto.image;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDto {
  private Long id;
  private String fileName;
  private String fileType;
  private Long productId;
  private String imageUrl;
}
