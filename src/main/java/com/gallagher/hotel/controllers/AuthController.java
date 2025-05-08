package com.gallagher.hotel.controllers;

import com.gallagher.hotel.dto.auth.AuthResponse;
import com.gallagher.hotel.dto.auth.LoginRequest;
import com.gallagher.hotel.dto.auth.RegisterRequest;
import com.gallagher.hotel.dto.responses.ApiResponse;
import com.gallagher.hotel.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "API pour l'authentification des utilisateurs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
        summary = "Inscription d'un nouvel utilisateur",
        description = "Permet à un utilisateur de créer un compte",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inscription réussie"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données d'inscription invalides", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Données d'inscription",
                required = true,
                content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            ) RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Inscription réussie", authResponse));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Connexion d'un utilisateur",
        description = "Authentifie un utilisateur et retourne un token JWT",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Données de connexion", 
                required = true,
                content = @Content(schema = @Schema(implementation = LoginRequest.class))
            ) LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Connexion réussie", authResponse));
    }
    
    // La déconnexion est gérée par Spring Security via le LogoutHandler
    // Voir JwtLogoutHandler et SecurityConfig pour l'implémentation
} 