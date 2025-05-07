package com.gallagher.hotel.mappers;

import com.gallagher.hotel.dto.PaymentDto;
import com.gallagher.hotel.dto.ReservationDto;
import com.gallagher.hotel.models.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    private final ReservationMapper reservationMapper;

    public PaymentMapper(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    public PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        ReservationDto reservationDto = null;
        if (payment.getReservation() != null) {
            reservationDto = reservationMapper.toDto(payment.getReservation());
        }
        
        return new PaymentDto(
                payment.getId(),
                payment.getTransactionId(),
                payment.getReservation() != null ? payment.getReservation().getId() : null,
                reservationDto,
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getPaymentDate(),
                payment.getPaymentDetails(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public Payment toEntity(PaymentDto dto) {
        if (dto == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setTransactionId(dto.getTransactionId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(dto.getStatus());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentDetails(dto.getPaymentDetails());
        
        // La réservation est définie séparément via la méthode setReservation

        return payment;
    }
} 