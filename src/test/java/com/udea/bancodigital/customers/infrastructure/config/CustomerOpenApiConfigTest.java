package com.udea.bancodigital.customers.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OpenApiCustomizer;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerOpenApiConfigTest {

    private final OpenApiCustomizer customizer =
            new CustomerOpenApiConfig().customerOpenApiCustomizer();

    @Test
    void customize_givenEmptyOpenApi_initialisesInfoAndComponentsAndRegistersSchemas() {
        OpenAPI openApi = new OpenAPI();

        customizer.customise(openApi);

        assertThat(openApi.getInfo())
                .isNotNull()
                .extracting(Info::getTitle, Info::getVersion)
                .containsExactly("Banco Digital API", "1.0.0");
        assertThat(openApi.getInfo().getContact().getEmail())
                .isEqualTo("soporte@bancodigital.com");
        assertThat(openApi.getComponents()).isNotNull();
        assertThat(openApi.getComponents().getSchemas()).containsKey("ClienteResponse");
        assertThat(openApi.getComponents().getResponses())
                .containsKeys("ClienteOK", "ClienteNotFound");
    }

    @Test
    void customize_givenPrePopulatedOpenApi_preservesExistingInfoAndComponents() {
        OpenAPI openApi = new OpenAPI();
        Info preExistingInfo = new Info().title("Pre-existing").version("9.9.9");
        Components preExistingComponents = new Components();
        openApi.setInfo(preExistingInfo);
        openApi.setComponents(preExistingComponents);

        customizer.customise(openApi);

        assertThat(openApi.getInfo()).isSameAs(preExistingInfo);
        assertThat(openApi.getInfo().getTitle()).isEqualTo("Pre-existing");
        assertThat(openApi.getComponents()).isSameAs(preExistingComponents);
        assertThat(openApi.getComponents().getSchemas()).containsKey("ClienteResponse");
        assertThat(openApi.getComponents().getResponses())
                .containsKeys("ClienteOK", "ClienteNotFound");
    }
}
