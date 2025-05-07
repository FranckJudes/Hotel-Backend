package com.gallagher.hotel.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Value("${server.port:8104}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestion des Hôtels")
                        .version("2.0")
                        .description("API complète pour la gestion d'hôtels incluant la gestion des utilisateurs, " +
                                "des chambres, des réservations, des paiements et des témoignages")
                        .contact(new Contact()
                                .name("Support Technique")
                                .email("support@hotelapi.com")
                                .url("https://www.hotelapi.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Serveur local de développement")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token d'authentification. Exemple: Bearer abcdef123456")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}