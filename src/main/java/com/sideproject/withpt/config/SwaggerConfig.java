package com.sideproject.withpt.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "WithPT API 명세서",
        description = "Spring MVC 기반 회원-트레이너 소통 서비스 API 명세서",
        version = "v1"
    )

)
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi memberRecordApi() {
        return GroupedOpenApi.builder()
            .group("member-record")
            .pathsToMatch("/api/v1/members/record/**")
            .build();
    }

    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
            .group("member")
            .pathsToMatch("/api/v1/members/**")
            .pathsToExclude("/api/v1/members/record/**")
            .build();
    }

    @Bean
    public GroupedOpenApi trainerApi() {
        return GroupedOpenApi.builder()
            .group("trainer")
            .pathsToMatch("/api/v1/trainers/**")
            .build();
    }

    @Bean
    public GroupedOpenApi oauthApi() {
        return GroupedOpenApi.builder()
            .group("oauth")
            .pathsToMatch("/api/v1/oauth/**")
            .build();
    }

    @Bean
    public GroupedOpenApi gymApi() {
        return GroupedOpenApi.builder()
            .group("gym")
            .pathsToMatch("/api/v1/gyms/**")
            .build();
    }

    @Bean
    public GroupedOpenApi personalTrainingApi() {
        return GroupedOpenApi.builder()
            .group("personal-training")
            .pathsToMatch("/api/v1/personal-trainings/**")
            .build();
    }

    @Bean
    public GroupedOpenApi lessonApi() {
        return GroupedOpenApi.builder()
            .group("lesson")
            .pathsToMatch("/api/v1/lessons/**")
            .build();
    }

    @Bean
    public GroupedOpenApi chatApi() {
        return GroupedOpenApi.builder()
            .group("chat")
            .pathsToMatch("/api/v1/chat/**")
            .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
            .group("all")
            .pathsToMatch("/api/v1/**")
            .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList("JWT"))
            .components(new Components()
                .addSecuritySchemes("JWT", createAPIKeyScheme())
            );
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
            .type(Type.HTTP)
            .bearerFormat("JWT")
            .scheme("Bearer");
    }
}
