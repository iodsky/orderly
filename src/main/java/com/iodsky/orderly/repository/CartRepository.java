package com.iodsky.orderly.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iodsky.orderly.model.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID> {

}
