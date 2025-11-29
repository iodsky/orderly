package com.iodsky.orderly.repository;

import com.iodsky.orderly.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByCategoryName(String category, Pageable pageable);

    Page<Product> findByBrand(String brand, Pageable pageable);

    Page<Product> findByName(String name, Pageable pageable);

    Page<Product> findByCategoryNameAndBrand(String category, String brand, Pageable pageable);

    Page<Product> findByBrandAndName(String brand, String name, Pageable pageable);

    Long countByBrandAndName(String brand, String name);

}
