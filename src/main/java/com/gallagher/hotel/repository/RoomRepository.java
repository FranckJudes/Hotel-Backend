package com.gallagher.hotel.repository;

import com.gallagher.hotel.enums.RoomStatus;
import com.gallagher.hotel.enums.RoomType;
import com.gallagher.hotel.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    Optional<Room> findByRoomNumber(String roomNumber);
    
    List<Room> findByType(RoomType type);
    
    List<Room> findByStatus(RoomStatus status);
    
    List<Room> findByCapacityGreaterThanEqual(int capacity);
    
    List<Room> findByPricePerNightLessThanEqual(BigDecimal maxPrice);
    
    @Query("SELECT r FROM Room r WHERE r.id NOT IN " +
           "(SELECT res.room.id FROM Reservation res WHERE " +
           "(:checkIn BETWEEN res.checkInDate AND res.checkOutDate) OR " +
           "(:checkOut BETWEEN res.checkInDate AND res.checkOutDate) OR " +
           "(res.checkInDate BETWEEN :checkIn AND :checkOut))")
    List<Room> findAvailableRooms(@Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);
    
    @Query("SELECT r FROM Room r WHERE r.type = :type AND r.id NOT IN " +
           "(SELECT res.room.id FROM Reservation res WHERE " +
           "(:checkIn BETWEEN res.checkInDate AND res.checkOutDate) OR " +
           "(:checkOut BETWEEN res.checkInDate AND res.checkOutDate) OR " +
           "(res.checkInDate BETWEEN :checkIn AND :checkOut))")
    List<Room> findAvailableRoomsByType(@Param("type") RoomType type, 
                                      @Param("checkIn") LocalDate checkIn, 
                                      @Param("checkOut") LocalDate checkOut);
}