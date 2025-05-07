package com.gallagher.hotel.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour l'inscription d'un nouvel utilisateur")
public class RegisterRequest {
    
    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Schema(description = "Nom d'utilisateur unique", example = "jean.dupont", required = true)
    private String username;
    
    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Schema(description = "Mot de passe (minimum 6 caractères)", example = "motdepasse123", required = true)
    private String password;
    
    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    @Schema(description = "Adresse email valide", example = "jean.dupont@example.com", required = true)
    private String email;
    
    @Schema(description = "Prénom de l'utilisateur", example = "Jean")
    private String firstName;
    
    @Schema(description = "Nom de famille de l'utilisateur", example = "Dupont")
    private String lastName;
    
    @Schema(description = "Numéro de téléphone", example = "0612345678")
    private String phoneNumber;
} 