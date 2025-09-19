package com.iodsky.orderly.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "carts")
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @EqualsAndHashCode.Exclude
  private Set<CartItem> items = new HashSet<>();

  @Transient
  public BigDecimal getTotalAmount() {
    return this.items.stream()
        .map(CartItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void addItem(CartItem item) {
    this.items.add(item);
    item.setCart(this);
  }

  public void removeItem(CartItem item) {
    this.items.remove(item);
    item.setCart(null);
  }

}
