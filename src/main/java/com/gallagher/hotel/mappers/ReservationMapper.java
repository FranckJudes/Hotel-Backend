package com.gallagher.hotel.mappers;

import com.gallagher.hotel.dto.ReservationDto;
import com.gallagher.hotel.dto.RoomDto;
import com.gallagher.hotel.dto.UserDto;
import com.gallagher.hotel.models.Reservation;
import com.gallagher.hotel.models.Room;
import com.gallagher.hotel.models.User;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private final RoomMapper roomMapper;
    private final UserMapper userMapper;

    public ReservationMapper(RoomMapper roomMapper, UserMapper userMapper) {
        this.roomMapper = roomMapper;
        this.userMapper = userMapper;
    }

    public ReservationDto toDto(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        UserDto userDto = null;
        if (reservation.getUser() != null) {
            userDto = userMapper.toDto(reservation.getUser());
        }

        RoomDto roomDto = null;
        if (reservation.getRoom() != null) {
            roomDto = roomMapper.toDto(reservation.getRoom());
        }

        return new ReservationDto(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getUser() != null ? reservation.getUser().getId() : null,
                userDto,
                reservation.getRoom() != null ? reservation.getRoom().getId() : null,
                roomDto,
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getNumberOfGuests(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getSpecialRequests(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    public Reservation toEntity(ReservationDto dto) {
        if (dto == null) {
            return null;
        }

        Reservation reservation = new Reservation();
        reservation.setId(dto.getId());
        reservation.setReservationNumber(dto.getReservationNumber());
        reservation.setCheckInDate(dto.getCheckInDate());
        reservation.setCheckOutDate(dto.getCheckOutDate());
        reservation.setNumberOfGuests(dto.getNumberOfGuests());
        reservation.setTotalPrice(dto.getTotalPrice());
        reservation.setStatus(dto.getStatus());
        reservation.setSpecialRequests(dto.getSpecialRequests());

        // Les entités User et Room sont définies séparément
        // via les méthodes setUser et setRoom
        
        return reservation;
    }
} 