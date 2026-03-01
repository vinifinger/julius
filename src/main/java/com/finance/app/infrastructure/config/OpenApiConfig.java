package com.finance.app.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Personal Finance API")
                                                .version("v1.0.0")
                                                .description("Personal finance management API — Julius. "
                                                                + "Built with Clean Architecture and DDD, "
                                                                + "providing management of users, accounts, categories, "
                                                                + "competences and financial transactions.")
                                                .contact(new Contact()
                                                                .name("Julius Finance")
                                                                .url("https://github.com/vinifinger/julius")))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"))
                                .components(new Components()
                                                .addSecuritySchemes("Bearer JWT", new SecurityScheme()
                                                                .name("Authorization")
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("Enter the JWT token obtained via POST /api/v1/auth/login")));
        }

}
