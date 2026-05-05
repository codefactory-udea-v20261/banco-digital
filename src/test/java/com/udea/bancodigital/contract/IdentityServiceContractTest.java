package com.udea.bancodigital.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PactConsumerTestExt.class)
class IdentityServiceContractTest {

    @Pact(consumer = "banco-digital-core-banking", provider = "banco-digital-identity")
    public V4Pact createPactForValidateToken(PactDslWithProvider builder) {
        return builder
                .uponReceiving("a request to validate a JWT token")
                .path("/api/v1/auth/validate-token")
                .method("POST")
                .headers(Map.of("Content-Type", "application/json"))
                .body("\"eyJhbGciOiJIUzI1NiJ9.test.signature\"")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .booleanType("active", true)
                        .stringType("sub", "testuser")
                        .stringValue("uid", "550e8400-e29b-41d4-a716-446655440000")
                        .stringValue("clienteId", "550e8400-e29b-41d4-a716-446655440001")
                        .array("authorities")
                        .stringValue("ROLE_CLIENTE")
                        .closeArray())
                .toPact(V4Pact.class);
    }

    @PactTestFor(pactMethod = "createPactForValidateToken")
    @Test
    void testValidateTokenContract(MockServer mockServer) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(mockServer.getUrl() + "/api/v1/auth/validate-token");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity("\"eyJhbGciOiJIUzI1NiJ9.test.signature\"", ContentType.APPLICATION_JSON));

            client.execute(request, response -> {
                assertEquals(200, response.getCode());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                    String body = reader.lines().collect(Collectors.joining());
                    assertTrue(body.contains("\"active\":true"));
                } catch (Exception e) {
                    fail("Failed to read response: " + e.getMessage());
                }
                return null;
            });
        }
    }

    @Pact(consumer = "banco-digital-core-banking", provider = "banco-digital-identity")
    public V4Pact createPactForProvisionAccess(PactDslWithProvider builder) {
        return builder
                .uponReceiving("a request to provision client access")
                .path("/api/v1/internal/users/provision-client-access")
                .method("POST")
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .stringType("clienteId", "550e8400-e29b-41d4-a716-446655440000")
                        .stringType("email", "cliente@banco.com"))
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .stringType("userId", "550e8400-e29b-41d4-a716-446655440000")
                        .stringType("status", "PROVISIONED"))
                .toPact(V4Pact.class);
    }

    @PactTestFor(pactMethod = "createPactForProvisionAccess")
    @Test
    void testProvisionAccessContract(MockServer mockServer) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(mockServer.getUrl() + "/api/v1/internal/users/provision-client-access");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity("{\"clienteId\": \"550e8400-e29b-41d4-a716-446655440000\", \"email\": \"cliente@banco.com\"}", ContentType.APPLICATION_JSON));

            client.execute(request, response -> {
                assertEquals(200, response.getCode());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                    String body = reader.lines().collect(Collectors.joining());
                    assertTrue(body.contains("\"status\":\"PROVISIONED\""));
                } catch (Exception e) {
                    fail("Failed to read response: " + e.getMessage());
                }
                return null;
            });
        }
    }
}
