package com.helen.api_crm.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                        .title("API CRM - Gestão de Vendas")
                        .version("1.0")
                        .description("API RESTful para gestão de clientes, vendedores e vendas com autenticação JWT.")
                        .contact(new Contact()
                                .name("Helen Cristina")
                                .email("h.c.batista2002@gmail.com")
                                .url("https://www.linkedin.com/in/hcbatista/"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))

                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
