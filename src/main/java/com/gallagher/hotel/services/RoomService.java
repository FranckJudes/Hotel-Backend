package com.gallagher.hotel.services;

import com.gallagher.hotel.dto.RoomDto;
import com.gallagher.hotel.enums.RoomType;
import com.gallagher.hotel.mappers.RoomMapper;
import com.gallagher.hotel.models.Room;
import com.gallagher.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée avec l'ID: " + id));
        return roomMapper.toDto(room);
    }

    @Transactional
    public RoomDto createRoom(RoomDto roomDto) {
        Room room = roomMapper.toEntity(roomDto);
        Room savedRoom = roomRepository.save(room);
        return roomMapper.toDto(savedRoom);
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomDto roomDto) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Chambre non trouvée avec l'ID: " + id);
        }
        Room room = roomMapper.toEntity(roomDto);
        room.setId(id);
        Room updatedRoom = roomRepository.save(room);
        return roomMapper.toDto(updatedRoom);
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Chambre non trouvée avec l'ID: " + id);
        }
        roomRepository.deleteById(id);
    }

    public List<RoomDto> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("La date de départ doit être après la date d'arrivée");
        }
        
        return roomRepository.findAvailableRooms(checkIn, checkOut)
                .stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RoomDto> getRoomsByType(RoomType type) {
        return roomRepository.findByType(type)
                .stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RoomDto> getRoomsByMinCapacity(int capacity) {
        return roomRepository.findByCapacityGreaterThanEqual(capacity)
                .stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }
} 