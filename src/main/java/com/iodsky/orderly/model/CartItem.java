package com.iodsky.orderly.model;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cart_items")
public class CartItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "cart_id")
  @EqualsAndHashCode.Exclude
  private Cart cart;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  private int quantity;

  private BigDecimal unitPrice;

  @Transient
  public BigDecimal getTotalPrice() {
    return this.unitPrice.multiply(BigDecimal.valueOf(quantity));
  }

}
