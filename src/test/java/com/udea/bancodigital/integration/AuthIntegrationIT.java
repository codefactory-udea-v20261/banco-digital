package com.udea.bancodigital.integration;

import com.udea.bancodigital.auth.application.dto.LoginRequestDto;
import com.udea.bancodigital.auth.application.dto.LoginResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("it")
class AuthIntegrationIT {

    static {
        // Try to set the docker API version before Testcontainers initializes
        try {
            System.setProperty("docker.api.version", "1.40");
            System.setProperty("DOCKER_API_VERSION", "1.40");

            // Try to set via reflection on DockerClientConfig
            Class<?> configClass = Class.forName("com.github.dockerjava.core.DefaultDockerClientConfig");
            Field field = configClass.getDeclaredField("DOCKER_API_VERSION");
            field.setAccessible(true);
            System.out.println("Docker API version field found: " + field);
        } catch (Exception e) {
            System.out.println("Could not set Docker API version via reflection: " + e.getMessage());
        }
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("banco_digital_core_it")
            .withUsername("test")
            .withPassword("test");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRejectInvalidLogin() {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .correo("nonexistent@test.com")
                .clave("wrongpassword")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDto> entity = new HttpEntity<>(loginRequest, headers);

        var response = restTemplate.postForEntity(
                "/api/v1/auth/login",
                entity,
                LoginResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldAccessProtectedEndpointWithoutToken() {
        var response = restTemplate.getForEntity(
                "/api/v1/clientes/me",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
