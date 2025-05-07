package com.gallagher.hotel.dto.testimonial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour la mise à jour d'un témoignage")
public class UpdateTestimonialRequest {

    @Schema(description = "Nouveau contenu du témoignage", example = "Séjour très agréable avec un personnel aux petits soins !")
    private String content;

    @Min(value = 1, message = "La note doit être entre 1 et 5")
    @Max(value = 5, message = "La note doit être entre 1 et 5")
    @Schema(description = "Nouvelle note attribuée (de 1 à 5)", example = "4", minimum = "1", maximum = "5")
    private Integer rating;
} 