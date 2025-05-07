package com.gallagher.hotel.dto.blog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de mise à jour d'un article de blog")
public class UpdateBlogPostRequest {
    
    @Size(min = 5, max = 100, message = "Le titre doit contenir entre 5 et 100 caractères")
    @Schema(description = "Nouveau titre de l'article", example = "Les meilleures activités à faire dans notre région")
    private String title;
    
    @Size(min = 100, message = "Le contenu doit contenir au moins 100 caractères")
    @Schema(description = "Nouveau contenu de l'article", example = "Notre région offre de nombreuses activités...")
    private String content;
    
    @Schema(description = "Nouvelle URL de l'image principale", example = "https://example.com/images/activities.jpg")
    private String featuredImage;
    
    @Schema(description = "Nouveaux tags associés à l'article")
    private List<String> tags = new ArrayList<>();
    
    @Schema(description = "Nouveau statut de publication", example = "true")
    private Boolean published;
} 