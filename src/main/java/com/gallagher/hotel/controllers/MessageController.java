package com.gallagher.hotel.controllers;

import com.gallagher.hotel.dto.MessageDto;
import com.gallagher.hotel.dto.message.CreateMessageRequest;
import com.gallagher.hotel.dto.message.UpdateMessageStatusRequest;
import com.gallagher.hotel.dto.responses.ApiResponse;
import com.gallagher.hotel.services.MessageService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "API pour la gestion des messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un message par son ID",
        description = "Les utilisateurs peuvent voir uniquement les messages dont ils sont l'expéditeur ou le destinataire",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<MessageDto>> getMessageById(
            @PathVariable @Parameter(description = "ID du message") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        MessageDto message = messageService.getMessageById(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Message trouvé", message));
    }

    @GetMapping("/received")
    @Operation(
        summary = "Récupérer les messages reçus",
        description = "Récupère la liste des messages dont l'utilisateur est le destinataire",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des messages reçus récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<List<MessageDto>>> getReceivedMessages(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageDto> messages = messageService.getReceivedMessages(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Liste des messages reçus récupérée avec succès", messages));
    }

    @GetMapping("/sent")
    @Operation(
        summary = "Récupérer les messages envoyés",
        description = "Récupère la liste des messages dont l'utilisateur est l'expéditeur",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des messages envoyés récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<List<MessageDto>>> getSentMessages(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageDto> messages = messageService.getSentMessages(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Liste des messages envoyés récupérée avec succès", messages));
    }

    @GetMapping("/received/paginated")
    @Operation(
        summary = "Récupérer les messages reçus avec pagination",
        description = "Récupère la liste paginée des messages dont l'utilisateur est le destinataire",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste paginée des messages reçus récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<Page<MessageDto>>> getPaginatedReceivedMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        Page<MessageDto> messages = messageService.getPaginatedReceivedMessages(userDetails, pageable);
        return ResponseEntity.ok(ApiResponse.success("Liste paginée des messages reçus récupérée avec succès", messages));
    }

    @GetMapping("/sent/paginated")
    @Operation(
        summary = "Récupérer les messages envoyés avec pagination",
        description = "Récupère la liste paginée des messages dont l'utilisateur est l'expéditeur",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste paginée des messages envoyés récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<Page<MessageDto>>> getPaginatedSentMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        Page<MessageDto> messages = messageService.getPaginatedSentMessages(userDetails, pageable);
        return ResponseEntity.ok(ApiResponse.success("Liste paginée des messages envoyés récupérée avec succès", messages));
    }

    @GetMapping("/conversation/{userId}")
    @Operation(
        summary = "Récupérer une conversation avec un utilisateur",
        description = "Récupère la liste des messages échangés entre l'utilisateur connecté et un autre utilisateur",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversation récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<MessageDto>>> getConversation(
            @PathVariable @Parameter(description = "ID de l'autre utilisateur") Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageDto> messages = messageService.getConversation(userId, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Conversation récupérée avec succès", messages));
    }

    @GetMapping("/count-unread")
    @Operation(
        summary = "Compter les messages non lus",
        description = "Retourne le nombre de messages non lus pour l'utilisateur connecté",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nombre de messages non lus récupéré avec succès")
        }
    )
    public ResponseEntity<ApiResponse<Long>> countUnreadMessages(
            @AuthenticationPrincipal UserDetails userDetails) {
        long count = messageService.countUnreadMessages(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Nombre de messages non lus récupéré avec succès", count));
    }

    @PostMapping
    @Operation(
        summary = "Envoyer un nouveau message",
        description = "Permet à un utilisateur d'envoyer un message à un autre utilisateur",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Message envoyé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Destinataire non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<MessageDto>> createMessage(
            @Valid @RequestBody @Parameter(description = "Données du message", 
                schema = @Schema(implementation = CreateMessageRequest.class)) CreateMessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MessageDto createdMessage = messageService.createMessage(request, userDetails);
        return new ResponseEntity<>(ApiResponse.success("Message envoyé avec succès", createdMessage), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Mettre à jour le statut de lecture d'un message",
        description = "Permet au destinataire de marquer un message comme lu ou non lu",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statut du message mis à jour avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<MessageDto>> updateMessageStatus(
            @PathVariable @Parameter(description = "ID du message") Long id,
            @Valid @RequestBody @Parameter(description = "Données de mise à jour", 
                schema = @Schema(implementation = UpdateMessageStatusRequest.class)) UpdateMessageStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MessageDto updatedMessage = messageService.updateMessageStatus(id, request, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Statut du message mis à jour avec succès", updatedMessage));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un message",
        description = "Permet à l'expéditeur ou au destinataire de supprimer un message",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message supprimé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable @Parameter(description = "ID du message") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        messageService.deleteMessage(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Message supprimé avec succès"));
    }
} 