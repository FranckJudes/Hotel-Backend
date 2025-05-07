package com.gallagher.hotel.dto.payment;

import com.gallagher.hotel.enums.PaymentStatus;
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
@Schema(description = "Requête pour la mise à jour du statut d'un paiement")
public class UpdatePaymentStatusRequest {

    @NotNull(message = "Le statut est requis")
    @Schema(description = "Nouveau statut du paiement", example = "COMPLETED", required = true, 
            allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "FAILED", "REFUNDED"})
    private PaymentStatus status;
} 