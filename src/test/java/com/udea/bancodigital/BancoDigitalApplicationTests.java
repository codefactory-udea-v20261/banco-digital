package com.udea.bancodigital;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

@SpringBootTest
@ActiveProfiles("test")
class BancoDigitalApplicationTests {

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring carga correctamente
        // con la configuración de seguridad abierta inicial
    }
}
