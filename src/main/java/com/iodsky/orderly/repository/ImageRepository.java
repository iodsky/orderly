package com.iodsky.orderly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iodsky.orderly.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

  Image findByFileName(String fileName);

}
