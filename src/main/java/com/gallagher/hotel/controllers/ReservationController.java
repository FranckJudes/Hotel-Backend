package com.gallagher.hotel.controllers;

import com.gallagher.hotel.dto.ReservationDto;
import com.gallagher.hotel.dto.reservation.CreateReservationRequest;
import com.gallagher.hotel.dto.reservation.UpdateReservationRequest;
import com.gallagher.hotel.dto.responses.ApiResponse;
import com.gallagher.hotel.enums.ReservationStatus;
import com.gallagher.hotel.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse.ApiResponseBuilder;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Gestion des Réservations", description = "API pour la gestion des réservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('RECEPTIONIST')")
    @Operation(
        summary = "Récupérer toutes les réservations",
        description = "Récupère la liste de toutes les réservations (réservé au personnel)",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des réservations récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getAllReservations() {
        List<ReservationDto> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(ApiResponse.success("Liste des réservations récupérée avec succès", reservations));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer une réservation par son ID",
        description = "Les clients peuvent voir uniquement leurs propres réservations",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Réservation non trouvée", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<ReservationDto>> getReservationById(
            @PathVariable @Parameter(description = "ID de la réservation") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReservationDto reservation = reservationService.getReservationById(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Réservation trouvée", reservation));
    }

    @PostMapping
    @Operation(
        summary = "Créer une nouvelle réservation",
        description = "Permet à un utilisateur authentifié de créer une réservation",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Réservation créée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Chambre non trouvée", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<ReservationDto>> createReservation(
            @Valid @RequestBody @Parameter(description = "Données de la réservation", 
                schema = @Schema(implementation = CreateReservationRequest.class)) 
            CreateReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Convertir CreateReservationRequest en ReservationDto
        ReservationDto reservationDto = ReservationDto.builder()
                .roomId(request.getRoomId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfGuests(request.getNumberOfGuests())
                .specialRequests(request.getSpecialRequests())
                .build();
        
        ReservationDto createdReservation = reservationService.createReservation(reservationDto, userDetails);
        return new ResponseEntity<>(ApiResponse.success("Réservation créée avec succès", createdReservation), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour une réservation existante",
        description = "Les utilisateurs peuvent modifier uniquement leurs propres réservations",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Réservation mise à jour avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Réservation non trouvée", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<ReservationDto>> updateReservation(
            @PathVariable @Parameter(description = "ID de la réservation") Long id,
            @Valid @RequestBody @Parameter(description = "Données de mise à jour", 
                schema = @Schema(implementation = UpdateReservationRequest.class)) 
            UpdateReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Convertir UpdateReservationRequest en ReservationDto
        ReservationDto reservationDto = ReservationDto.builder()
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfGuests(request.getNumberOfGuests())
                .specialRequests(request.getSpecialRequests())
                .status(request.getStatus())
                .build();
        
        ReservationDto updatedReservation = reservationService.updateReservation(id, reservationDto, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Réservation mise à jour avec succès", updatedReservation));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Annuler une réservation",
        description = "Les utilisateurs peuvent annuler uniquement leurs propres réservations",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Réservation annulée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Réservation non trouvée", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @PathVariable @Parameter(description = "ID de la réservation") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        reservationService.cancelReservation(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Réservation annulée avec succès"));
    }

    @GetMapping("/user")
    @Operation(
        summary = "Récupérer les réservations de l'utilisateur connecté",
        description = "Récupère la liste des réservations de l'utilisateur authentifié",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des réservations de l'utilisateur récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getCurrentUserReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ReservationDto> userReservations = reservationService.getUserReservations(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Liste des réservations de l'utilisateur récupérée avec succès", userReservations));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('RECEPTIONIST')")
    @Operation(
        summary = "Récupérer les réservations par statut",
        description = "Récupère toutes les réservations correspondant au statut spécifié",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des réservations récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByStatus(
            @PathVariable @Parameter(description = "Statut des réservations", 
                schema = @Schema(allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"}))
            ReservationStatus status) {
        List<ReservationDto> reservations = reservationService.getReservationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Liste des réservations par statut récupérée avec succès", reservations));
    }

    @GetMapping("/date")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('RECEPTIONIST')")
    @Operation(
        summary = "Récupérer les réservations par date",
        description = "Récupère les réservations pour une date d'arrivée ou de départ spécifiée",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des réservations récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            @Parameter(description = "Date d'arrivée ou de départ", example = "2023-12-24") LocalDate date) {
        List<ReservationDto> reservations = reservationService.getReservationsByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Liste des réservations par date récupérée avec succès", reservations));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('RECEPTIONIST')")
    @Operation(
        summary = "Mettre à jour le statut d'une réservation",
        description = "Permet au personnel de modifier le statut d'une réservation",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statut de la réservation mis à jour avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Réservation non trouvée", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<ReservationDto>> updateReservationStatus(
            @PathVariable @Parameter(description = "ID de la réservation") Long id,
            @RequestParam @Parameter(description = "Nouveau statut de la réservation", 
                schema = @Schema(allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"})) 
            ReservationStatus status) {
        ReservationDto updatedReservation = reservationService.updateReservationStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Statut de la réservation mis à jour avec succès", updatedReservation));
    }
} 