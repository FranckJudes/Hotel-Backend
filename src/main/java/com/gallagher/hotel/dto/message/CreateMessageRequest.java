package com.gallagher.hotel.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création d'un message")
public class CreateMessageRequest {
    
    @NotNull(message = "L'identifiant du destinataire est obligatoire")
    @Schema(description = "Identifiant du destinataire", example = "2")
    private Long recipientId;
    
    @NotBlank(message = "Le sujet est obligatoire")
    @Size(min = 3, max = 100, message = "Le sujet doit contenir entre 3 et 100 caractères")
    @Schema(description = "Sujet du message", example = "Question concernant ma réservation")
    private String subject;
    
    @NotBlank(message = "Le contenu est obligatoire")
    @Size(min = 10, message = "Le contenu doit contenir au moins 10 caractères")
    @Schema(description = "Contenu du message", example = "Bonjour, j'aimerais savoir si je peux modifier ma réservation du 24 au 30 décembre.")
    private String content;
} 