package com.gallagher.hotel.controllers;

import com.gallagher.hotel.dto.BlogPostDto;
import com.gallagher.hotel.dto.blog.CreateBlogPostRequest;
import com.gallagher.hotel.dto.blog.UpdateBlogPostRequest;
import com.gallagher.hotel.dto.responses.ApiResponse;
import com.gallagher.hotel.services.BlogPostService;
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
@RequestMapping("/api/v1/blog")
@RequiredArgsConstructor
@Tag(name = "Blog", description = "API pour la gestion des articles de blog")
public class BlogPostController {

    private final BlogPostService blogPostService;

    @GetMapping("/public")
    @Operation(
        summary = "Récupérer tous les articles publiés",
        description = "Accessible publiquement, retourne tous les articles qui ont été publiés",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des articles récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<List<BlogPostDto>>> getAllPublishedPosts() {
        List<BlogPostDto> posts = blogPostService.getAllPublishedPosts();
        return ResponseEntity.ok(ApiResponse.success("Liste des articles publiés récupérée avec succès", posts));
    }

    @GetMapping("/public/paginated")
    @Operation(
        summary = "Récupérer les articles publiés avec pagination",
        description = "Accessible publiquement, retourne les articles publiés avec pagination",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Articles paginés récupérés avec succès")
        }
    )
    public ResponseEntity<ApiResponse<Page<BlogPostDto>>> getPaginatedPublishedPosts(Pageable pageable) {
        Page<BlogPostDto> posts = blogPostService.getPaginatedPublishedPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success("Articles paginés récupérés avec succès", posts));
    }

    @GetMapping("/public/{id}")
    @Operation(
        summary = "Récupérer un article par son ID",
        description = "Retourne le détail d'un article publié",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Article trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Article non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<BlogPostDto>> getPublishedPostById(
            @PathVariable @Parameter(description = "ID de l'article") Long id) {
        BlogPostDto post = blogPostService.getPostById(id);
        return ResponseEntity.ok(ApiResponse.success("Article trouvé", post));
    }

    @GetMapping("/public/search")
    @Operation(
        summary = "Rechercher des articles",
        description = "Recherche dans les titres et contenus des articles publiés",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés avec succès")
        }
    )
    public ResponseEntity<ApiResponse<Page<BlogPostDto>>> searchPosts(
            @RequestParam @Parameter(description = "Mot-clé à rechercher", example = "activités") String keyword,
            Pageable pageable) {
        Page<BlogPostDto> posts = blogPostService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Résultats de recherche récupérés avec succès", posts));
    }

    @GetMapping("/public/tag/{tag}")
    @Operation(
        summary = "Récupérer les articles par tag",
        description = "Retourne tous les articles publiés ayant le tag spécifié",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des articles par tag récupérée avec succès")
        }
    )
    public ResponseEntity<ApiResponse<Page<BlogPostDto>>> getPostsByTag(
            @PathVariable @Parameter(description = "Tag à rechercher", example = "activités") String tag,
            Pageable pageable) {
        Page<BlogPostDto> posts = blogPostService.getPostsByTag(tag, pageable);
        return ResponseEntity.ok(ApiResponse.success("Liste des articles par tag récupérée avec succès", posts));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Récupérer un article par son ID (admin)",
        description = "Accessible uniquement aux administrateurs et managers, peut récupérer des articles non publiés",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Article trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Article non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<BlogPostDto>> getPostByIdAdmin(
            @PathVariable @Parameter(description = "ID de l'article") Long id) {
        BlogPostDto post = blogPostService.getPostByIdAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("Article trouvé", post));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Créer un nouvel article",
        description = "Permet aux administrateurs et managers de créer un nouvel article",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Article créé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<BlogPostDto>> createPost(
            @Valid @RequestBody @Parameter(description = "Données de l'article", 
                schema = @Schema(implementation = CreateBlogPostRequest.class)) CreateBlogPostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        BlogPostDto createdPost = blogPostService.createPost(request, userDetails);
        return new ResponseEntity<>(ApiResponse.success("Article créé avec succès", createdPost), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Mettre à jour un article",
        description = "Permet aux administrateurs et managers de modifier un article",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Article mis à jour avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Article non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<BlogPostDto>> updatePost(
            @PathVariable @Parameter(description = "ID de l'article") Long id,
            @Valid @RequestBody @Parameter(description = "Données de mise à jour", 
                schema = @Schema(implementation = UpdateBlogPostRequest.class)) UpdateBlogPostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        BlogPostDto updatedPost = blogPostService.updatePost(id, request, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Article mis à jour avec succès", updatedPost));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Supprimer un article",
        description = "Permet aux administrateurs et managers de supprimer un article",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Article supprimé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Article non trouvé", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable @Parameter(description = "ID de l'article") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        blogPostService.deletePost(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Article supprimé avec succès"));
    }

    @GetMapping("/user")
    @Operation(
        summary = "Récupérer les articles de l'utilisateur connecté",
        description = "Permet à un utilisateur de récupérer ses propres articles",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des articles de l'utilisateur récupérée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        }
    )
    public ResponseEntity<ApiResponse<List<BlogPostDto>>> getUserPosts(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<BlogPostDto> userPosts = blogPostService.getUserPosts(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Liste des articles de l'utilisateur récupérée avec succès", userPosts));
    }
} 