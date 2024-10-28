package org.example.firstlabis.config.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // Определяем схему безопасности для Bearer токенов
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Добавляем требования безопасности для всех endpoints
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

        return new OpenAPI()
                .info(new Info().title("API Documentation").version("1.0"))
                .addSecurityItem(securityRequirement)
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("Authorization", securityScheme));
    }
}
