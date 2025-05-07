package com.gallagher.hotel.dto.payment;

import com.gallagher.hotel.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour la création d'un paiement")
public class CreatePaymentRequest {

    @NotNull(message = "L'ID de la réservation est requis")
    @Schema(description = "Identifiant de la réservation associée au paiement", example = "1", required = true)
    private Long reservationId;

    @NotNull(message = "Le montant est requis")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Schema(description = "Montant du paiement", example = "250.00", required = true)
    private BigDecimal amount;

    @NotNull(message = "La méthode de paiement est requise")
    @Schema(description = "Méthode de paiement", example = "CREDIT_CARD", required = true, allowableValues = {"CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER", "CASH", "PAYPAL"})
    private PaymentMethod paymentMethod;

    @Schema(description = "Détails du paiement (numéro de transaction, référence, etc.)", example = "Transaction #4829405")
    private String paymentDetails;
} 