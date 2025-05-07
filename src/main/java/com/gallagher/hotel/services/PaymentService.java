package com.gallagher.hotel.services;

import com.gallagher.hotel.dto.PaymentDto;
import com.gallagher.hotel.enums.PaymentStatus;
import com.gallagher.hotel.enums.ReservationStatus;
import com.gallagher.hotel.mappers.PaymentMapper;
import com.gallagher.hotel.models.Payment;
import com.gallagher.hotel.models.Reservation;
import com.gallagher.hotel.models.User;
import com.gallagher.hotel.repository.PaymentRepository;
import com.gallagher.hotel.repository.ReservationRepository;
import com.gallagher.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    public PaymentDto getPaymentById(Long id, UserDetails userDetails) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID: " + id));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est autorisé à voir ce paiement
        boolean isAdmin = user.getRole().name().equals("ADMIN") || 
                         user.getRole().name().equals("MANAGER");
        
        if (!isAdmin && !payment.getReservation().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à accéder à ce paiement");
        }

        return paymentMapper.toDto(payment);
    }

    @Transactional
    public PaymentDto createPayment(PaymentDto paymentDto, UserDetails userDetails) {
        // Récupérer la réservation associée
        Reservation reservation = reservationRepository.findById(paymentDto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + paymentDto.getReservationId()));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est autorisé à créer un paiement pour cette réservation
        boolean isAdmin = user.getRole().name().equals("ADMIN") || 
                         user.getRole().name().equals("MANAGER") ||
                         user.getRole().name().equals("RECEPTIONIST");
        
        if (!isAdmin && !reservation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à créer un paiement pour cette réservation");
        }

        // Créer le paiement
        Payment payment = paymentMapper.toEntity(paymentDto);
        payment.setTransactionId(generateTransactionId());
        payment.setReservation(reservation);
        payment.setPaymentDate(LocalDateTime.now());
        
        // Si non défini, mettre le statut par défaut
        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }

        // Sauvegarder le paiement
        Payment savedPayment = paymentRepository.save(payment);
        
        // Si le paiement est complété, mettre à jour le statut de la réservation
        if (PaymentStatus.COMPLETED.equals(payment.getStatus())) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        }
        
        return paymentMapper.toDto(savedPayment);
    }

    @Transactional
    public PaymentDto updatePaymentStatus(Long id, PaymentStatus status, UserDetails userDetails) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID: " + id));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est autorisé à modifier ce paiement
        boolean isAdmin = user.getRole().name().equals("ADMIN") || 
                         user.getRole().name().equals("MANAGER");
        
        if (!isAdmin) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier le statut d'un paiement");
        }

        payment.setStatus(status);
        
        // Si le paiement est complété, mettre à jour le statut de la réservation
        if (PaymentStatus.COMPLETED.equals(status)) {
            Reservation reservation = payment.getReservation();
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(updatedPayment);
    }

    public List<PaymentDto> getPaymentsByReservationId(Long reservationId, UserDetails userDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + reservationId));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est autorisé à voir les paiements de cette réservation
        boolean isAdmin = user.getRole().name().equals("ADMIN") || 
                         user.getRole().name().equals("MANAGER") ||
                         user.getRole().name().equals("RECEPTIONIST");
        
        if (!isAdmin && !reservation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à accéder aux paiements de cette réservation");
        }

        return paymentRepository.findByReservation(reservation)
                .stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    public BigDecimal getMonthlyRevenue(int year, int month) {
        return paymentRepository.sumCompletedPaymentsForMonth(month, year);
    }

    public BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.sumCompletedPaymentsBetweenDates(start, end);
    }

    private String generateTransactionId() {
        return "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 