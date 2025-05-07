package com.gallagher.hotel.repository;

import com.gallagher.hotel.enums.ReservationStatus;
import com.gallagher.hotel.models.Reservation;
import com.gallagher.hotel.models.Room;
import com.gallagher.hotel.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    
    List<Reservation> findByUser(User user);
    
    List<Reservation> findByRoom(Room room);
    
    List<Reservation> findByStatus(ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate = :date OR r.checkOutDate = :date")
    List<Reservation> findByCheckInOrCheckOutDate(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate BETWEEN :startDate AND :endDate")
    List<Reservation> findByCheckInDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId AND " +
           "((r.checkInDate BETWEEN :checkIn AND :checkOut) OR " +
           "(r.checkOutDate BETWEEN :checkIn AND :checkOut) OR " +
           "(:checkIn BETWEEN r.checkInDate AND r.checkOutDate))")
    List<Reservation> findOverlappingReservations(@Param("roomId") Long roomId, 
                                           @Param("checkIn") LocalDate checkIn, 
                                           @Param("checkOut") LocalDate checkOut);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.status = 'CONFIRMED' AND YEAR(r.checkInDate) = :year AND MONTH(r.checkInDate) = :month")
    long countConfirmedReservationsForMonth(@Param("year") int year, @Param("month") int month);
} 