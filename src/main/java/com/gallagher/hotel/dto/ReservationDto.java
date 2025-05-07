package com.gallagher.hotel.dto;

import com.gallagher.hotel.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long id;
    private String reservationNumber;
    private Long userId;
    private UserDto user;
    private Long roomId;
    private RoomDto room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private String specialRequests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 