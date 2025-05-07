package com.gallagher.hotel.dto.reservation;

import com.gallagher.hotel.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour la mise à jour d'une réservation")
public class UpdateReservationRequest {

    @Future(message = "La date d'arrivée doit être dans le futur")
    @Schema(description = "Nouvelle date d'arrivée", example = "2023-12-24")
    private LocalDate checkInDate;

    @Future(message = "La date de départ doit être dans le futur")
    @Schema(description = "Nouvelle date de départ", example = "2023-12-30")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Le nombre d'invités doit être d'au moins 1")
    @Schema(description = "Nouveau nombre de personnes", example = "2")
    private Integer numberOfGuests;

    @Schema(description = "Nouvelles demandes spéciales", example = "Chambre avec vue sur la mer et petit-déjeuner")
    private String specialRequests;
    
    @Schema(description = "Nouveau statut de la réservation (uniquement pour les admins/managers)", example = "CONFIRMED", allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"})
    private ReservationStatus status;
} 