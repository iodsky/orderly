package com.iodsky.orderly.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.iodsky.orderly.enums.OrderStatus;

import jakarta.persistence.*;
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
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private LocalDateTime dateOrdered;
  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @Builder.Default
  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private Set<OrderItem> items = new HashSet<>();

}
