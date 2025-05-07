package com.gallagher.hotel.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour la connexion d'un utilisateur")
public class LoginRequest {
    
    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Schema(description = "Nom d'utilisateur", example = "jean.dupont", required = true)
    private String username;
    
    @NotBlank(message = "Le mot de passe est requis")
    @Schema(description = "Mot de passe", example = "motdepasse123", required = true)
    private String password;
} 