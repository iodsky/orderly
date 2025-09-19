package com.iodsky.orderly.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iodsky.orderly.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

}
