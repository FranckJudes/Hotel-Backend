package com.gallagher.hotel.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de mise à jour du statut d'un message")
public class UpdateMessageStatusRequest {
    
    @NotNull(message = "Le statut de lecture est obligatoire")
    @Schema(description = "Statut de lecture du message", example = "true")
    private Boolean read;
} 