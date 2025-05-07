package com.gallagher.hotel.dto.testimonial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour la création d'un témoignage")
public class CreateTestimonialRequest {

    @NotBlank(message = "Le contenu du témoignage est requis")
    @Schema(description = "Contenu du témoignage", example = "Excellent séjour, personnel accueillant et cadre magnifique !", required = true)
    private String content;

    @NotNull(message = "La note est requise")
    @Min(value = 1, message = "La note doit être entre 1 et 5")
    @Max(value = 5, message = "La note doit être entre 1 et 5")
    @Schema(description = "Note attribuée (de 1 à 5)", example = "5", required = true, minimum = "1", maximum = "5")
    private Integer rating;
} 