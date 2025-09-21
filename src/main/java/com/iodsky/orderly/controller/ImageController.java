package com.iodsky.orderly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.iodsky.orderly.dto.image.ImageDto;
import com.iodsky.orderly.dto.mapper.ImageMapper;
import com.iodsky.orderly.model.Image;
import com.iodsky.orderly.service.image.ImageService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

  private final ImageService imageService;
  private final ImageMapper imageMapper;

  @PostMapping()
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<ImageDto>> uploadImage(
      @RequestParam() List<MultipartFile> images,
      @RequestParam() UUID productId) {
    List<ImageDto> savedImages = imageService.saveImage(images, productId).stream()
        .map(imageMapper::toDto)
        .toList();
    return new ResponseEntity<>(savedImages, HttpStatus.CREATED);
  }

  @GetMapping("{id}")
  public ResponseEntity<ByteArrayResource> getImageById(@PathVariable() UUID id) {
    Image image = imageService.getImageById(id);
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFileName() + "\"")
        .body(new ByteArrayResource(image.getImage()));
  }

  @PutMapping("{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ImageDto> updateImage(@PathVariable() UUID id, @RequestParam() MultipartFile image) {
    ImageDto updatedImage = imageMapper.toDto(imageService.updateImage(image, id));
    return ResponseEntity.ok(updatedImage);
  }

  @DeleteMapping("{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> deleteImage(@PathVariable() UUID id) {
    imageService.deleteImageById(id);
    return ResponseEntity.ok("Image " + id + " deleted successfully");
  }

}
