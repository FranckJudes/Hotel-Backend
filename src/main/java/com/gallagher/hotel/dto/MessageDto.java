package com.gallagher.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Données d'un message")
public class MessageDto {
    
    @Schema(description = "Identifiant du message")
    private Long id;
    
    @Schema(description = "Identifiant de l'expéditeur")
    private Long senderId;
    
    @Schema(description = "Expéditeur du message")
    private UserDto sender;
    
    @Schema(description = "Identifiant du destinataire")
    private Long recipientId;
    
    @Schema(description = "Destinataire du message")
    private UserDto recipient;
    
    @Schema(description = "Sujet du message", example = "Question concernant ma réservation")
    private String subject;
    
    @Schema(description = "Contenu du message", example = "Bonjour, j'aimerais savoir si...")
    private String content;
    
    @Schema(description = "Indique si le message a été lu", example = "false")
    private boolean read;
    
    @Schema(description = "Date de lecture du message")
    private LocalDateTime readAt;
    
    @Schema(description = "Date d'envoi du message")
    private LocalDateTime createdAt;
} 