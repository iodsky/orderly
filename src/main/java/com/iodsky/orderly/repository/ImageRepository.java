package com.iodsky.orderly.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iodsky.orderly.model.Image;

public interface ImageRepository extends JpaRepository<Image, UUID> {

  Image findByFileName(String fileName);

}
