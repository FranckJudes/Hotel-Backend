package com.gallagher.hotel.controllers;

import com.gallagher.hotel.dto.TestimonialDto;
import com.gallagher.hotel.dto.responses.ApiResponse;
import com.gallagher.hotel.dto.testimonial.CreateTestimonialRequest;
import com.gallagher.hotel.dto.testimonial.UpdateTestimonialRequest;
import com.gallagher.hotel.services.TestimonialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/testimonials")
@RequiredArgsConstructor
@Tag(name = "Gestion des Témoignages", description = "API pour la gestion des témoignages clients")
public class TestimonialController {

    private final TestimonialService testimonialService;

    @GetMapping("/public")
    @Operation(
        summary = "Récupérer tous les témoignages approuvés",
        description = "Accessible publiquement, retourne tous les témoignages qui ont été approuvés",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des témoignages récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<List<TestimonialDto>>> getAllApprovedTestimonials() {
        List<TestimonialDto> testimonials = testimonialService.getAllApprovedTestimonials();
        return ResponseEntity.ok(ApiResponse.success("Liste des témoignages approuvés récupérée avec succès", testimonials));
    }

    @GetMapping("/public/paginated")
    @Operation(
        summary = "Récupérer les témoignages approuvés avec pagination",
        description = "Accessible publiquement, retourne les témoignages approuvés avec pagination",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Témoignages paginés récupérés avec succès")
        }
    )
    public ResponseEntity<ApiResponse<Page<TestimonialDto>>> getPaginatedApprovedTestimonials(Pageable pageable) {
        Page<TestimonialDto> testimonials = testimonialService.getPaginatedApprovedTestimonials(pageable);
        return ResponseEntity.ok(ApiResponse.success("Témoignages paginés récupérés avec succès", testimonials));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Récupérer les témoignages en attente d'approbation",
        description = "Accessible uniquement aux administrateurs et managers",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des témoignages en attente récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<TestimonialDto>>> getPendingTestimonials() {
        List<TestimonialDto> testimonials = testimonialService.getPendingTestimonials();
        return ResponseEntity.ok(ApiResponse.success("Liste des témoignages en attente récupérée avec succès", testimonials));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un témoignage par son ID",
        description = "Retourne le détail d'un témoignage",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Témoignage trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Témoignage non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<TestimonialDto>> getTestimonialById(
            @PathVariable @Parameter(description = "ID du témoignage") Long id) {
        TestimonialDto testimonial = testimonialService.getTestimonialById(id);
        return ResponseEntity.ok(ApiResponse.success("Témoignage trouvé", testimonial));
    }

    @PostMapping
    @Operation(
        summary = "Créer un nouveau témoignage",
        description = "Permet à un utilisateur authentifié de soumettre un témoignage",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Témoignage créé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<TestimonialDto>> createTestimonial(
            @Valid @RequestBody @Parameter(description = "Données du témoignage", 
                schema = @Schema(implementation = CreateTestimonialRequest.class)) CreateTestimonialRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Convertir CreateTestimonialRequest en TestimonialDto
        TestimonialDto testimonialDto = TestimonialDto.builder()
                .content(request.getContent())
                .rating(request.getRating())
                .build();
                
        TestimonialDto createdTestimonial = testimonialService.createTestimonial(testimonialDto, userDetails);
        return new ResponseEntity<>(ApiResponse.success("Témoignage créé avec succès", createdTestimonial), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Approuver un témoignage",
        description = "Permet aux administrateurs et managers d'approuver un témoignage",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Témoignage approuvé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Témoignage non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<TestimonialDto>> approveTestimonial(
            @PathVariable @Parameter(description = "ID du témoignage") Long id) {
        TestimonialDto approvedTestimonial = testimonialService.approveTestimonial(id);
        return ResponseEntity.ok(ApiResponse.success("Témoignage approuvé avec succès", approvedTestimonial));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour un témoignage",
        description = "Les utilisateurs peuvent modifier leurs propres témoignages",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Témoignage mis à jour avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Témoignage non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<TestimonialDto>> updateTestimonial(
            @PathVariable @Parameter(description = "ID du témoignage") Long id,
            @Valid @RequestBody @Parameter(description = "Données du témoignage", 
                schema = @Schema(implementation = UpdateTestimonialRequest.class)) UpdateTestimonialRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Convertir UpdateTestimonialRequest en TestimonialDto
        TestimonialDto testimonialDto = TestimonialDto.builder()
                .content(request.getContent())
                .rating(request.getRating())
                .build();
                
        TestimonialDto updatedTestimonial = testimonialService.updateTestimonial(id, testimonialDto, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Témoignage mis à jour avec succès", updatedTestimonial));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un témoignage",
        description = "Les utilisateurs peuvent supprimer leurs propres témoignages, les admins peuvent supprimer n'importe quel témoignage",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Témoignage supprimé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Témoignage non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Void>> deleteTestimonial(
            @PathVariable @Parameter(description = "ID du témoignage") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        testimonialService.deleteTestimonial(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Témoignage supprimé avec succès"));
    }

    @GetMapping("/user")
    @Operation(
        summary = "Récupérer les témoignages de l'utilisateur connecté",
        description = "Permet à un utilisateur de récupérer ses propres témoignages",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des témoignages de l'utilisateur récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<TestimonialDto>>> getUserTestimonials(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<TestimonialDto> userTestimonials = testimonialService.getUserTestimonials(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Liste des témoignages de l'utilisateur récupérée avec succès", userTestimonials));
    }

    @GetMapping("/public/rating")
    @Operation(
        summary = "Obtenir la note moyenne",
        description = "Retourne la moyenne des notes des témoignages approuvés",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Note moyenne calculée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<Double>> getAverageRating() {
        Double averageRating = testimonialService.getAverageRating();
        return ResponseEntity.ok(ApiResponse.success("Note moyenne calculée avec succès", averageRating));
    }
} 