package com.iodsky.orderly.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iodsky.orderly.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

}
