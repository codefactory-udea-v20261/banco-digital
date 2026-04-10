package com.udea.bancodigital;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Disabled in CI because Testcontainers requires a Docker daemon, which is not available in the current environment.")
class BancoDigitalApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring carga correctamente
        // con la configuración de seguridad abierta inicial
    }
}
