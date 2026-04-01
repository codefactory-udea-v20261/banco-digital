package com.udea.bancodigital.accounts.infrastructure.config;


import com.udea.bancodigital.accounts.application.usecase.CrearCuentaUseCase;
import com.udea.bancodigital.accounts.domain.port.in.CrearCuentaPort;
import com.udea.bancodigital.accounts.domain.port.out.ClienteServicePort;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CuentaServiceConfig {

    @Bean
    public CrearCuentaPort crearCuentaPort(
            CuentaRepositoryPort cuentaRepositoryPort,
            ClienteServicePort clienteServicePort) {

        return new CrearCuentaUseCase(cuentaRepositoryPort, clienteServicePort);
    }
}
