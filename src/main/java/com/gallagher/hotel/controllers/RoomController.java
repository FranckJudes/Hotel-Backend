package com.gallagher.hotel.controllers;

import com.gallagher.hotel.dto.RoomDto;
import com.gallagher.hotel.dto.responses.ApiResponse;
import com.gallagher.hotel.enums.RoomType;
import com.gallagher.hotel.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Gestion des Chambres", description = "API pour la gestion des chambres de l'hôtel")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @Operation(
        summary = "Récupérer toutes les chambres",
        description = "Retourne la liste de toutes les chambres disponibles",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des chambres récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<List<RoomDto>>> getAllRooms() {
        List<RoomDto> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(ApiResponse.success("Liste des chambres récupérée avec succès", rooms));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer une chambre par son ID",
        description = "Retourne les détails d'une chambre spécifique",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Chambre trouvée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Chambre non trouvée", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<RoomDto>> getRoomById(
            @PathVariable @Parameter(description = "ID de la chambre") Long id) {
        RoomDto room = roomService.getRoomById(id);
        return ResponseEntity.ok(ApiResponse.success("Chambre trouvée", room));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Créer une nouvelle chambre",
        description = "Permet aux administrateurs et managers d'ajouter une nouvelle chambre",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Chambre créée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<RoomDto>> createRoom(
            @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Données de la chambre",
                required = true,
                content = @Content(schema = @Schema(implementation = RoomDto.class))
            ) RoomDto roomDto) {
        RoomDto createdRoom = roomService.createRoom(roomDto);
        return new ResponseEntity<>(ApiResponse.success("Chambre créée avec succès", createdRoom), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Mettre à jour une chambre",
        description = "Permet aux administrateurs et managers de modifier les informations d'une chambre",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Chambre mise à jour avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Chambre non trouvée", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<RoomDto>> updateRoom(
            @PathVariable @Parameter(description = "ID de la chambre") Long id,
            @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Données mises à jour de la chambre",
                required = true,
                content = @Content(schema = @Schema(implementation = RoomDto.class))
            ) RoomDto roomDto) {
        RoomDto updatedRoom = roomService.updateRoom(id, roomDto);
        return ResponseEntity.ok(ApiResponse.success("Chambre mise à jour avec succès", updatedRoom));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Supprimer une chambre",
        description = "Permet aux administrateurs de supprimer une chambre",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Chambre supprimée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Chambre non trouvée", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable @Parameter(description = "ID de la chambre") Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Chambre supprimée avec succès"));
    }

    @GetMapping("/available")
    @Operation(
        summary = "Rechercher des chambres disponibles",
        description = "Retourne les chambres disponibles pour une période spécifiée",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des chambres disponibles récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<List<RoomDto>>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            @Parameter(description = "Date d'arrivée", example = "2023-12-24") LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            @Parameter(description = "Date de départ", example = "2023-12-30") LocalDate checkOut) {
        List<RoomDto> availableRooms = roomService.findAvailableRooms(checkIn, checkOut);
        return ResponseEntity.ok(ApiResponse.success("Liste des chambres disponibles récupérée avec succès", availableRooms));
    }

    @GetMapping("/type/{type}")
    @Operation(
        summary = "Récupérer les chambres par type",
        description = "Retourne toutes les chambres d'un type spécifié",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des chambres par type récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<List<RoomDto>>> getRoomsByType(
            @PathVariable @Parameter(description = "Type de chambre", example = "STANDARD") String type) {
        List<RoomDto> rooms = roomService.getRoomsByType(RoomType.valueOf(type));
        return ResponseEntity.ok(ApiResponse.success("Liste des chambres par type récupérée avec succès", rooms));
    }

    @GetMapping("/capacity/{capacity}")
    @Operation(
        summary = "Récupérer les chambres par capacité minimale",
        description = "Retourne toutes les chambres ayant une capacité supérieure ou égale à la capacité spécifiée",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des chambres par capacité minimale récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<List<RoomDto>>> getRoomsByMinCapacity(
            @PathVariable @Parameter(description = "Capacité minimale de la chambre", example = "2") int capacity) {
        List<RoomDto> rooms = roomService.getRoomsByMinCapacity(capacity);
        return ResponseEntity.ok(ApiResponse.success("Liste des chambres par capacité minimale récupérée avec succès", rooms));
    }
} 