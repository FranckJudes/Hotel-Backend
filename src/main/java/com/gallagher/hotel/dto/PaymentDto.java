package com.gallagher.hotel.dto;

import com.gallagher.hotel.enums.PaymentMethod;
import com.gallagher.hotel.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private String transactionId;
    private Long reservationId;
    private ReservationDto reservation;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String paymentDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 