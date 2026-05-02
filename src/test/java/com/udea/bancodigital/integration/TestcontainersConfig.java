package com.udea.bancodigital.integration;

import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Properties;

public class TestcontainersConfig {
    static {
        // Load properties before Testcontainers initializes
        Properties props = System.getProperties();
        props.setProperty("docker.api.version", "1.40");
        props.setProperty("DOCKER_API_VERSION", "1.40");
    }
}
