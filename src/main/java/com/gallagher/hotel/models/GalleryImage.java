package com.gallagher.hotel.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    @Column(nullable = false)
    private String imageUrl;
    
    private String category;
    
    private boolean featured;
    
    private int displayOrder;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 