package com.gallagher.hotel.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour obtenir les statistiques de revenus mensuels")
public class RevenueRequest {

    @NotNull(message = "L'année est requise")
    @Min(value = 2000, message = "L'année doit être valide")
    @Max(value = 2100, message = "L'année doit être valide")
    @Schema(description = "Année pour laquelle obtenir les revenus", example = "2023", required = true)
    private Integer year;

    @NotNull(message = "Le mois est requis")
    @Min(value = 1, message = "Le mois doit être entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être entre 1 et 12")
    @Schema(description = "Mois pour lequel obtenir les revenus (1-12)", example = "7", required = true, minimum = "1", maximum = "12")
    private Integer month;
} 