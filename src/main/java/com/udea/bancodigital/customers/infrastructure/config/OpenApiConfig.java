package com.udea.bancodigital.customers.infrastructure.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.examples.Example;
import static java.util.Map.entry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        // SCHEMA BASADO EN ClienteResponseDto
        Schema<?> clienteSchema = new Schema<>()
                .type("object")
                .addProperties("id", new StringSchema().example("550e8400-e29b-41d4-a716-446655440000"))
                .addProperties("numeroCedula", new StringSchema().example("123456789"))
                .addProperties("primerNombre", new StringSchema().example("Juan"))
                .addProperties("segundoNombre", new StringSchema().example("Carlos"))
                .addProperties("primerApellido", new StringSchema().example("Pérez"))
                .addProperties("segundoApellido", new StringSchema().example("Gómez"))
                .addProperties("email", new StringSchema().example("juan.perez@email.com"))
                .addProperties("telefono", new StringSchema().example("3001234567"))
                .addProperties("fechaNacimiento", new DateSchema().example("1990-05-20"))
                .addProperties("activo", new BooleanSchema().example(true))
                .addProperties("createdAt", new DateTimeSchema().example("2026-01-01T10:00:00Z"));

        // RESPONSE 200
        ApiResponse okResponse = new ApiResponse()
                .description("Cliente encontrado")
                .content(new Content().addMediaType("application/json",
                        new MediaType()
                                .schema(clienteSchema)
                                .examples(Map.of(
                                        "cliente",
                                        new Example()
                                                .summary("Cliente encontrado")
                                                .value(Map.ofEntries(
                                                        entry("id", "550e8400-e29b-41d4-a716-446655440000"),
                                                        entry("numeroCedula", "123456789"),
                                                        entry("primerNombre", "Juan"),
                                                        entry("segundoNombre", "Carlos"),
                                                        entry("primerApellido", "Pérez"),
                                                        entry("segundoApellido", "Gómez"),
                                                        entry("email", "juan.perez@email.com"),
                                                        entry("telefono", "3001234567"),
                                                        entry("fechaNacimiento", "1990-05-20"),
                                                        entry("activo", true),
                                                        entry("createdAt", "2026-01-01T10:00:00Z")
                                                ))
                                ))
                ));

        // RESPONSE 404
        ApiResponse notFoundResponse = new ApiResponse()
                .description("Cliente no encontrado")
                .content(new Content().addMediaType("application/json",
                        new MediaType()
                                .schema(new Schema<>()
                                        .type("object")
                                        .addProperties("message", new StringSchema().example("Cliente no encontrado"))
                                )
                                .examples(Map.of(
                                        "error",
                                        new Example()
                                                .summary("Cliente no existe")
                                                .value(Map.of(
                                                        "message", "Cliente no encontrado"
                                                ))
                                ))
                ));

        return new OpenAPI()
                .info(new Info()
                        .title("Banco Digital API")
                        .version("1.0.0")
                        .description("HU-02 - Consulta de información de cliente")
                        .contact(new Contact()
                                .name("Equipo Banco Digital")
                                .email("soporte@bancodigital.com")))
                .components(new Components()
                        .addSchemas("ClienteResponse", clienteSchema)
                        .addResponses("ClienteOK", okResponse)
                        .addResponses("ClienteNotFound", notFoundResponse));
    }
}
