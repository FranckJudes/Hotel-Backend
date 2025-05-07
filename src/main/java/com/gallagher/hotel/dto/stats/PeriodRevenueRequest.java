package com.gallagher.hotel.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour obtenir les statistiques de revenus sur une période")
public class PeriodRevenueRequest {

    @NotNull(message = "La date de début est requise")
    @PastOrPresent(message = "La date de début doit être dans le passé ou le présent")
    @Schema(description = "Date de début de la période", example = "2023-01-01T00:00:00", required = true)
    private LocalDateTime start;

    @NotNull(message = "La date de fin est requise")
    @Schema(description = "Date de fin de la période", example = "2023-12-31T23:59:59", required = true)
    private LocalDateTime end;
} 