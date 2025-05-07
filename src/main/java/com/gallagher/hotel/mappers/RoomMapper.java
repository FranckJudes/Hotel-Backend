package com.gallagher.hotel.mappers;

import com.gallagher.hotel.dto.RoomDto;
import com.gallagher.hotel.models.Room;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class RoomMapper {

    public RoomDto toDto(Room room) {
        if (room == null) {
            return null;
        }
        
        return new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getType(),
                room.getCapacity(),
                room.getPricePerNight(),
                room.getDescription(),
                room.getStatus(),
                room.isHasAirConditioning(),
                room.isHasTV(),
                room.isHasMinibar(),
                room.isHasSafe(),
                room.isHasWifi(),
                room.getImageUrls()
        );
    }

    public Room toEntity(RoomDto roomDto) {
        if (roomDto == null) {
            return null;
        }
        
        Room room = new Room();
        room.setId(roomDto.getId());
        room.setRoomNumber(roomDto.getRoomNumber());
        room.setType(roomDto.getType());
        room.setCapacity(roomDto.getCapacity());
        room.setPricePerNight(roomDto.getPricePerNight());
        room.setDescription(roomDto.getDescription());
        room.setStatus(roomDto.getStatus());
        room.setHasAirConditioning(roomDto.isHasAirConditioning());
        room.setHasTV(roomDto.isHasTV());
        room.setHasMinibar(roomDto.isHasMinibar());
        room.setHasSafe(roomDto.isHasSafe());
        room.setHasWifi(roomDto.isHasWifi());
        room.setReservations(new HashSet<>());
        
        if (roomDto.getImageUrls() != null) {
            room.setImageUrls(roomDto.getImageUrls());
        } else {
            room.setImageUrls(new HashSet<>());
        }
        
        return room;
    }
} 