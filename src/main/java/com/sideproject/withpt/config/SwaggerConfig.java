package com.sideproject.withpt.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
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
    public GroupedOpenApi publicApi() {
        String[] paths = {"/api/v1/**"};

        return GroupedOpenApi.builder()
            .group("WithPT-API v1")
            .pathsToMatch(paths)
            .build();
    }


}
