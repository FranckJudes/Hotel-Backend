package com.gallagher.hotel.repository;

import com.gallagher.hotel.enums.PaymentStatus;
import com.gallagher.hotel.models.Payment;
import com.gallagher.hotel.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByReservation(Reservation reservation);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumCompletedPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND MONTH(p.paymentDate) = :month AND YEAR(p.paymentDate) = :year")
    BigDecimal sumCompletedPaymentsForMonth(@Param("month") int month, @Param("year") int year);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'FAILED' AND p.paymentDate BETWEEN :startDate AND :endDate")
    long countFailedPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 