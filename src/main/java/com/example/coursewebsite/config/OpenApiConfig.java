package com.example.coursewebsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courseWebAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Course Web App API")
                        .version("v1")
                        .description("OpenAPI documentation for the Course Web App"));
    }
}
