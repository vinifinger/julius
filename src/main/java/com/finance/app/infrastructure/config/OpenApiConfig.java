package com.finance.app.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
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
                        .description("API de controle financeiro pessoal — Julius. "
                                + "Construída com Clean Architecture e DDD, "
                                + "oferecendo gerenciamento de usuários, contas, categorias, "
                                + "competências e transações financeiras.")
                        .contact(new Contact()
                                .name("Julius Finance")
                                .url("https://github.com/vinifinger/julius")));
    }

}
