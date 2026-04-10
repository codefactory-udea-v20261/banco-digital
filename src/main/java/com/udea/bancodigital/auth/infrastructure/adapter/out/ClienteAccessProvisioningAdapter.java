package com.udea.bancodigital.auth.infrastructure.adapter.out;

import com.udea.bancodigital.auth.application.dto.ProvisionClientAccessRequestDto;
import com.udea.bancodigital.auth.domain.port.in.ProvisionClientAccessPort;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessProvisioningPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteAccessProvisioningAdapter implements ClienteAccessProvisioningPort {

    private final ProvisionClientAccessPort provisionClientAccessPort;

    @Override
    public boolean existsByEmail(String email) {
        return provisionClientAccessPort.existsByEmail(email);
    }

    @Override
    public void provisionAccess(UUID clienteId, String email) {
        provisionClientAccessPort.provisionClientAccess(
                ProvisionClientAccessRequestDto.builder()
                        .clienteId(clienteId)
                        .email(email)
                        .build()
        );
    }
}
