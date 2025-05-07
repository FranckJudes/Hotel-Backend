package com.gallagher.hotel.models;

import com.gallagher.hotel.enums.RoomStatus;
import com.gallagher.hotel.enums.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String roomNumber;
    
    @Enumerated(EnumType.STRING)
    private RoomType type;
    
    private int capacity;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerNight;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
    
    private boolean hasAirConditioning;
    
    private boolean hasTV;
    
    private boolean hasMinibar;
    
    private boolean hasSafe;
    
    private boolean hasWifi;
    
    @OneToMany(mappedBy = "room")
    private Set<Reservation> reservations = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "room_images", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "image_url")
    private Set<String> imageUrls = new HashSet<>();
} 