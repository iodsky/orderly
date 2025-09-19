package com.iodsky.orderly.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iodsky.orderly.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {

  Image findByFileName(String fileName);

}
