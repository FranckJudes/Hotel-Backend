package com.gallagher.hotel.controllers;

import com.gallagher.hotel.dto.StatisticDto;
import com.gallagher.hotel.dto.responses.ApiResponse;
import com.gallagher.hotel.dto.stats.OccupancyRateRequest;
import com.gallagher.hotel.dto.stats.PeriodRevenueRequest;
import com.gallagher.hotel.dto.stats.RevenueRequest;
import com.gallagher.hotel.enums.StatisticType;
import com.gallagher.hotel.services.PaymentService;
import com.gallagher.hotel.services.ReservationService;
import com.gallagher.hotel.services.StatisticService;
import com.gallagher.hotel.services.TestimonialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
@Tag(name = "Statistiques", description = "API pour les statistiques de l'hôtel")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {

    private final StatisticService statisticService;
    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final TestimonialService testimonialService;

    @PostMapping("/revenue/monthly")
    @Operation(
        summary = "Revenu mensuel",
        description = "Retourne le revenu pour un mois et une année spécifiés",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<BigDecimal>> getMonthlyRevenue(
            @Valid @RequestBody @Parameter(description = "Paramètres de recherche", 
                schema = @Schema(implementation = RevenueRequest.class)) RevenueRequest request) {
        BigDecimal revenue = paymentService.getMonthlyRevenue(request.getYear(), request.getMonth());
        return ResponseEntity.ok(ApiResponse.success("Revenus mensuels récupérés avec succès", revenue));
    }

    @GetMapping("/revenue/yearly")
    @Operation(
        summary = "Revenus mensuels pour une année",
        description = "Retourne les revenus mensuels pour une année spécifiée",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Map<Integer, BigDecimal>>> getYearlyRevenueByMonth(
            @RequestParam @Parameter(description = "Année", example = "2023") int year) {
        Map<Integer, BigDecimal> yearlyRevenue = statisticService.getMonthlyRevenuesForYear(year);
        return ResponseEntity.ok(ApiResponse.success("Revenus mensuels pour l'année récupérés avec succès", yearlyRevenue));
    }

    @PostMapping("/occupancy")
    @Operation(
        summary = "Taux d'occupation",
        description = "Retourne le taux d'occupation pour une période donnée",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOccupancyRate(
            @Valid @RequestBody @Parameter(description = "Paramètres de recherche", 
                schema = @Schema(implementation = OccupancyRateRequest.class)) OccupancyRateRequest request) {
        Map<String, Object> occupancyData = statisticService.calculateOccupancyRate(request.getStartDate(), request.getEndDate());
        return ResponseEntity.ok(ApiResponse.success("Taux d'occupation récupéré avec succès", occupancyData));
    }

    @GetMapping("/reservations/monthly")
    @Operation(
        summary = "Nombre de réservations mensuelles",
        description = "Retourne le nombre de réservations pour un mois et une année spécifiés",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Long>> getMonthlyReservationsCount(
            @RequestParam @Parameter(description = "Année", example = "2023") int year,
            @RequestParam @Parameter(description = "Mois (1-12)", example = "7") int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        Long reservationsCount = statisticService.getMonthlyReservationsCount(yearMonth);
        return ResponseEntity.ok(ApiResponse.success("Nombre de réservations mensuelles récupéré avec succès", reservationsCount));
    }

    @GetMapping("/customer-satisfaction")
    @Operation(
        summary = "Satisfaction client",
        description = "Retourne la note moyenne des témoignages",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Double>> getCustomerSatisfaction() {
        Double averageRating = testimonialService.getAverageRating();
        return ResponseEntity.ok(ApiResponse.success("Note moyenne de satisfaction client récupérée avec succès", averageRating));
    }

    @GetMapping("/dashboard")
    @Operation(
        summary = "Statistiques du tableau de bord",
        description = "Retourne un ensemble de statistiques pour le tableau de bord",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> dashboardStats = statisticService.getDashboardStatistics();
        return ResponseEntity.ok(ApiResponse.success("Statistiques du tableau de bord récupérées avec succès", dashboardStats));
    }

    @PostMapping("/by-type")
    @Operation(
        summary = "Statistiques par type",
        description = "Retourne les statistiques d'un type spécifique pour une période donnée",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<StatisticDto>>> getStatsByType(
            @RequestParam @Parameter(description = "Type de statistique", 
                schema = @Schema(allowableValues = {"REVENUE", "OCCUPANCY_RATE", "BOOKINGS_COUNT", "CUSTOMER_SATISFACTION"})) 
                StatisticType type,
            @Valid @RequestBody @Parameter(description = "Paramètres de recherche", 
                schema = @Schema(implementation = OccupancyRateRequest.class)) OccupancyRateRequest request) {
        List<StatisticDto> statistics = statisticService.getStatisticsByTypeAndPeriod(type, request.getStartDate(), request.getEndDate());
        return ResponseEntity.ok(ApiResponse.success("Statistiques par type récupérées avec succès", statistics));
    }
} 