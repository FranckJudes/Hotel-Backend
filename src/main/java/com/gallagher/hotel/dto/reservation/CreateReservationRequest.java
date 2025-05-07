package com.gallagher.hotel.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour la création d'une réservation")
public class CreateReservationRequest {

    @NotNull(message = "L'ID de la chambre est requis")
    @Schema(description = "Identifiant de la chambre à réserver", example = "1", required = true)
    private Long roomId;

    @NotNull(message = "La date d'arrivée est requise")
    @Future(message = "La date d'arrivée doit être dans le futur")
    @Schema(description = "Date d'arrivée", example = "2023-12-24", required = true)
    private LocalDate checkInDate;

    @NotNull(message = "La date de départ est requise")
    @Future(message = "La date de départ doit être dans le futur")
    @Schema(description = "Date de départ", example = "2023-12-30", required = true)
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Le nombre d'invités doit être d'au moins 1")
    @Schema(description = "Nombre de personnes", example = "2", defaultValue = "1", required = true)
    private Integer numberOfGuests;

    @Schema(description = "Demandes spéciales pour la réservation", example = "Chambre avec vue sur la mer")
    private String specialRequests;
} 