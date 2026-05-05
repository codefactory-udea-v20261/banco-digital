package com.udea.bancodigital.infrastructure.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGroupsConfig {

    @Bean
    public GroupedOpenApi customersApi() {
        return buildGroup(
                "clientes",
                "/api/v1/clientes/**",
                "Banco Digital API - Clientes",
                "API de gestion del ciclo de vida de clientes: registro, consulta y actualizacion de perfil."
        );
    }

    @Bean
    public GroupedOpenApi accountsApi() {
        return buildGroup(
                "cuentas",
                "/api/v1/cuentas/**",
                "Banco Digital API - Cuentas",
                "API de administracion de cuentas y consultas de saldo para clientes autenticados."
        );
    }

    @Bean
    public GroupedOpenApi transactionsApi() {
        return buildGroup(
                "transacciones",
                "/api/v1/transacciones/**",
                "Banco Digital API - Transacciones",
                "API de movimientos, retiros y consultas transaccionales del core bancario."
        );
    }

    @Bean
    public GroupedOpenApi transfersApi() {
        return buildGroup(
                "transferencias",
                "/api/v1/transferencias/**",
                "Banco Digital API - Transferencias",
                "API de transferencias entre cuentas administradas por el core bancario."
        );
    }

    private GroupedOpenApi buildGroup(String group, String pathPattern, String title, String description) {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch(pathPattern)
                .addOpenApiCustomizer(openApi -> openApi.info(
                        new Info()
                                .title(title)
                                .version("v1.0.0")
                                .description(description)))
                .build();
    }
}
