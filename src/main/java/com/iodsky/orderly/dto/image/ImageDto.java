package com.iodsky.orderly.dto.image;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDto {
  private UUID id;
  private String fileName;
  private String fileType;
  private UUID productId;
  private String imageUrl;
}
