package com.udea.bancodigital.transactions.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.udea.bancodigital.accounts.infrastructure.repository.CuentaJpaRepository;
import com.udea.bancodigital.transactions.application.usecase.TransferirDineroUseCase;
import com.udea.bancodigital.transactions.domain.port.in.TransferirDineroPort;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;

@Configuration
public class TransaccionServiceConfig {

    @Bean
    public TransferirDineroPort transferirDineroPort(
            CuentaJpaRepository cuentaRepository,
            TransaccionRepositoryPort transaccionRepository) {

        return new TransferirDineroUseCase(
                cuentaRepository,
                transaccionRepository
        );
    }

}
