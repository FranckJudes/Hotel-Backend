package com.gallagher.hotel.services;

import com.gallagher.hotel.dto.ReservationDto;
import com.gallagher.hotel.enums.ReservationStatus;
import com.gallagher.hotel.mappers.ReservationMapper;
import com.gallagher.hotel.models.Reservation;
import com.gallagher.hotel.models.Room;
import com.gallagher.hotel.models.User;
import com.gallagher.hotel.repository.ReservationRepository;
import com.gallagher.hotel.repository.RoomRepository;
import com.gallagher.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public ReservationDto getReservationById(Long id, UserDetails userDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + id));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est autorisé à voir cette réservation
        boolean isAdmin = user.getRole().name().equals("ADMIN") || 
                         user.getRole().name().equals("MANAGER") || 
                         user.getRole().name().equals("RECEPTIONIST");
        
        if (!isAdmin && !reservation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à accéder à cette réservation");
        }

        return reservationMapper.toDto(reservation);
    }

    @Transactional
    public ReservationDto createReservation(ReservationDto reservationDto, UserDetails userDetails) {
        // Récupérer l'utilisateur connecté
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer la chambre
        Room room = roomRepository.findById(reservationDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée avec l'ID: " + reservationDto.getRoomId()));

        // Vérifier la disponibilité de la chambre pour ces dates
        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                room.getId(), 
                reservationDto.getCheckInDate(), 
                reservationDto.getCheckOutDate());
                
        if (!overlapping.isEmpty()) {
            throw new RuntimeException("La chambre n'est pas disponible pour ces dates");
        }

        // Créer la réservation
        Reservation reservation = reservationMapper.toEntity(reservationDto);
        reservation.setReservationNumber(generateReservationNumber());
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setStatus(ReservationStatus.PENDING);

        // Sauvegarder la réservation
        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(savedReservation);
    }

    @Transactional
    public ReservationDto updateReservation(Long id, ReservationDto reservationDto, UserDetails userDetails) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + id));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est autorisé à modifier cette réservation
        boolean isAdmin = user.getRole().name().equals("ADMIN") || 
                         user.getRole().name().equals("MANAGER") || 
                         user.getRole().name().equals("RECEPTIONIST");
        
        if (!isAdmin && !existingReservation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cette réservation");
        }

        // Mise à jour des champs modifiables
        if (reservationDto.getCheckInDate() != null) {
            existingReservation.setCheckInDate(reservationDto.getCheckInDate());
        }
        
        if (reservationDto.getCheckOutDate() != null) {
            existingReservation.setCheckOutDate(reservationDto.getCheckOutDate());
        }
        
        if (reservationDto.getNumberOfGuests() > 0) {
            existingReservation.setNumberOfGuests(reservationDto.getNumberOfGuests());
        }
        
        if (reservationDto.getSpecialRequests() != null) {
            existingReservation.setSpecialRequests(reservationDto.getSpecialRequests());
        }
        
        if (isAdmin && reservationDto.getStatus() != null) {
            existingReservation.setStatus(reservationDto.getStatus());
        }

        // Sauvegarder les modifications
        Reservation updatedReservation = reservationRepository.save(existingReservation);
        return reservationMapper.toDto(updatedReservation);
    }

    @Transactional
    public void cancelReservation(Long id, UserDetails userDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + id));
                
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est autorisé à annuler cette réservation
        boolean isAdmin = user.getRole().name().equals("ADMIN") || 
                         user.getRole().name().equals("MANAGER") || 
                         user.getRole().name().equals("RECEPTIONIST");
                         
        if (!isAdmin && !reservation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à annuler cette réservation");
        }

        // Annuler la réservation
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public List<ReservationDto> getUserReservations(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                
        return reservationRepository.findByUser(user)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservationDto> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservationDto> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByCheckInOrCheckOutDate(date)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDto updateReservationStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + id));
                
        reservation.setStatus(status);
        Reservation updatedReservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(updatedReservation);
    }

    private String generateReservationNumber() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 