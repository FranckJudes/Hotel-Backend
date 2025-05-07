package com.gallagher.hotel.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Requête pour obtenir les statistiques de taux d'occupation")
public class OccupancyRateRequest {

    @NotNull(message = "La date de début est requise")
    @Schema(description = "Date de début de la période", example = "2023-01-01", required = true)
    private LocalDate startDate;

    @NotNull(message = "La date de fin est requise")
    @Schema(description = "Date de fin de la période", example = "2023-12-31", required = true)
    private LocalDate endDate;
} 