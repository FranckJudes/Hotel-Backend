package com.gallagher.hotel.controllers;

import com.gallagher.hotel.dto.PaymentDto;
import com.gallagher.hotel.dto.payment.CreatePaymentRequest;
import com.gallagher.hotel.dto.payment.UpdatePaymentStatusRequest;
import com.gallagher.hotel.dto.responses.ApiResponse;
import com.gallagher.hotel.dto.stats.PeriodRevenueRequest;
import com.gallagher.hotel.dto.stats.RevenueRequest;
import com.gallagher.hotel.enums.PaymentStatus;
import com.gallagher.hotel.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Gestion des Paiements", description = "API pour la gestion des paiements")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Récupérer tous les paiements",
        description = "Accessible uniquement aux administrateurs et managers",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des paiements récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getAllPayments() {
        List<PaymentDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.success("Liste des paiements récupérée avec succès", payments));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un paiement par son ID",
        description = "Les clients peuvent voir seulement leurs propres paiements",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paiement trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Paiement non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(
            @PathVariable @Parameter(description = "ID du paiement") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        PaymentDto payment = paymentService.getPaymentById(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Paiement trouvé", payment));
    }

    @PostMapping
    @Operation(
        summary = "Créer un nouveau paiement",
        description = "Permet d'enregistrer un paiement pour une réservation",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Paiement créé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<PaymentDto>> createPayment(
            @Valid @RequestBody @Parameter(description = "Données du paiement", 
                schema = @Schema(implementation = CreatePaymentRequest.class)) CreatePaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Convertir CreatePaymentRequest en PaymentDto
        PaymentDto paymentDto = PaymentDto.builder()
                .reservationId(request.getReservationId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentDetails(request.getPaymentDetails())
                .build();
                
        PaymentDto createdPayment = paymentService.createPayment(paymentDto, userDetails);
        return new ResponseEntity<>(ApiResponse.success("Paiement créé avec succès", createdPayment), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Mettre à jour le statut d'un paiement",
        description = "Accessible uniquement aux administrateurs et managers",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statut du paiement mis à jour avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Paiement non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<PaymentDto>> updatePaymentStatus(
            @PathVariable @Parameter(description = "ID du paiement") Long id,
            @Valid @RequestBody @Parameter(description = "Données de mise à jour", 
                schema = @Schema(implementation = UpdatePaymentStatusRequest.class)) UpdatePaymentStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        PaymentDto updatedPayment = paymentService.updatePaymentStatus(id, request.getStatus(), userDetails);
        return ResponseEntity.ok(ApiResponse.success("Statut du paiement mis à jour avec succès", updatedPayment));
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(
        summary = "Récupérer tous les paiements pour une réservation",
        description = "Les clients peuvent voir seulement les paiements de leurs propres réservations",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des paiements récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Réservation non trouvée", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByReservationId(
            @PathVariable @Parameter(description = "ID de la réservation") Long reservationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PaymentDto> payments = paymentService.getPaymentsByReservationId(reservationId, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Liste des paiements pour la réservation récupérée avec succès", payments));
    }

    @PostMapping("/revenue/monthly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Obtenir les revenus mensuels",
        description = "Récupère le montant total des paiements complétés pour un mois spécifique",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Revenus calculés avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<BigDecimal>> getMonthlyRevenue(
            @Valid @RequestBody @Parameter(description = "Paramètres de recherche", 
                schema = @Schema(implementation = RevenueRequest.class)) RevenueRequest request) {
        BigDecimal revenue = paymentService.getMonthlyRevenue(request.getYear(), request.getMonth());
        return ResponseEntity.ok(ApiResponse.success("Revenus mensuels calculés avec succès", revenue));
    }

    @PostMapping("/revenue/period")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Obtenir les revenus pour une période",
        description = "Récupère le montant total des paiements complétés entre deux dates",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Revenus calculés avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<BigDecimal>> getPeriodRevenue(
            @Valid @RequestBody @Parameter(description = "Paramètres de recherche", 
                schema = @Schema(implementation = PeriodRevenueRequest.class)) PeriodRevenueRequest request) {
        BigDecimal revenue = paymentService.getTotalRevenue(request.getStart(), request.getEnd());
        return ResponseEntity.ok(ApiResponse.success("Revenus pour la période calculés avec succès", revenue));
    }
} 