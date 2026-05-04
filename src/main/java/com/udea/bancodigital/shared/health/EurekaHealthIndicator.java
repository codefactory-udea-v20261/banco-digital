package com.udea.bancodigital.shared.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Custom health indicator for Eureka service discovery.
 * Checks if this service is properly registered and can discover other services.
 */
@Slf4j
@Component("eurekaHealth")
@RequiredArgsConstructor
public class EurekaHealthIndicator implements HealthIndicator {

    private static final String EUREKA_KEY = EUREKA_KEY;


    private final DiscoveryClient discoveryClient;

    @Override
    public Health health() {
        try {
            // List all registered services
            List<String> services = discoveryClient.getServices();
            
            if (services == null || services.isEmpty()) {
                log.warn("No services found in service registry");
                return Health.down()
                        .withDetail(EUREKA_KEY, "No services registered")
                        .build();
            }

            log.debug("Eureka health check passed. Registered services: {}", services.size());
            return Health.up()
                    .withDetail(EUREKA_KEY, "Connected")
                    .withDetail("registered_services", services.size())
                    .withDetail("services", services)
                    .build();

        } catch (Exception e) {
            log.error("Eureka health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail(EUREKA_KEY, "Connection failed")
                    .withDetail("reason", e.getMessage())
                    .build();
        }
    }
}
