package com.udea.bancodigital.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestClientConfigTest {

    @Test
    void restTemplate_Creation() {
        RestClientConfig config = new RestClientConfig();
        RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
        RestTemplate restTemplate = new RestTemplate();
        
        when(builder.setConnectTimeout(any(Duration.class))).thenReturn(builder);
        when(builder.setReadTimeout(any(Duration.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);
        
        RestTemplate result = config.restTemplate(builder);
        
        assertNotNull(result);
        verify(builder).setConnectTimeout(Duration.ofSeconds(5));
        verify(builder).setReadTimeout(Duration.ofSeconds(10));
    }
}
