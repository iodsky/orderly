package com.iodsky.orderly.model;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String fileName;
    private String fileType;

    @Lob
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
