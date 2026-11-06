package com.juniverse.hexagonal.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Configuración de la documentación OpenAPI (Swagger UI). */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    /** Datos de la API (título, contacto, licencia) y servidores de ejemplo; cambia las URL por las reales. */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hexagonal Architecture CRUD API")
                        .description("REST API for User management using Hexagonal Architecture with Spring Boot 3 and MySQL8")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Java Universe")
                                .url("https://github.com/programming0026"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080" + contextPath)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.example.com" + contextPath)
                                .description("Production Server")
                ));
    }
}