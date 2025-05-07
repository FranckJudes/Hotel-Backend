package com.gallagher.hotel.dto;

import com.gallagher.hotel.enums.RoomStatus;
import com.gallagher.hotel.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String roomNumber;
    private RoomType type;
    private int capacity;
    private BigDecimal pricePerNight;
    private String description;
    private RoomStatus status;
    private boolean hasAirConditioning;
    private boolean hasTV;
    private boolean hasMinibar;
    private boolean hasSafe;
    private boolean hasWifi;
    private Set<String> imageUrls;
} 