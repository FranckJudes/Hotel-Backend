package com.gallagher.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Données d'un article de blog")
public class BlogPostDto {
    
    @Schema(description = "Identifiant de l'article")
    private Long id;
    
    @Schema(description = "Titre de l'article", example = "Les meilleures activités à faire dans notre région")
    private String title;
    
    @Schema(description = "Contenu de l'article", example = "Notre région offre de nombreuses activités...")
    private String content;
    
    @Schema(description = "Identifiant de l'auteur")
    private Long authorId;
    
    @Schema(description = "Auteur de l'article")
    private UserDto author;
    
    @Schema(description = "URL de l'image principale", example = "https://example.com/images/activities.jpg")
    private String featuredImage;
    
    @Schema(description = "Tags associés à l'article")
    private List<String> tags = new ArrayList<>();
    
    @Schema(description = "Statut de publication", example = "true")
    private boolean published;
    
    @Schema(description = "Date de publication")
    private LocalDateTime publishedAt;
    
    @Schema(description = "Date de création")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date de dernière modification")
    private LocalDateTime updatedAt;
} 