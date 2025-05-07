package com.gallagher.hotel.dto.blog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Requête de création d'un article de blog")
public class CreateBlogPostRequest {
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 100, message = "Le titre doit contenir entre 5 et 100 caractères")
    @Schema(description = "Titre de l'article", example = "Les meilleures activités à faire dans notre région")
    private String title;
    
    @NotBlank(message = "Le contenu est obligatoire")
    @Size(min = 100, message = "Le contenu doit contenir au moins 100 caractères")
    @Schema(description = "Contenu de l'article", example = "Notre région offre de nombreuses activités...")
    private String content;
    
    @Schema(description = "URL de l'image principale", example = "https://example.com/images/activities.jpg")
    private String featuredImage;
    
    @Schema(description = "Tags associés à l'article")
    private List<String> tags = new ArrayList<>();
    
    @Schema(description = "Indique si l'article doit être publié", example = "true", defaultValue = "false")
    private boolean published = false;
} 