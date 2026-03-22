package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.shared.util.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  HU1 — Registro de nuevos clientes                          ║
 * ║  Líder: Santiago | Validaciones: Carlos                     ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * REGLA ARQUITECTÓNICA (ADR-001):
 * Este caso de uso vive en application/ y orquesta el dominio.
 * No conoce nada de Spring MVC ni JPA directamente.
 * Solo habla con puertos (interfaces), nunca con adaptadores concretos.
 */
@Service
@RequiredArgsConstructor
public class CrearClienteUseCase implements UseCase<CrearClienteRequestDto, ClienteResponseDto> {

    // Puerto de salida — la implementación real (JPA) está en infrastructure/
    private final ClienteRepositoryPort clienteRepository;

    @Override
    @Transactional
    public ClienteResponseDto ejecutar(CrearClienteRequestDto request) {
        // ── 1. Validar unicidad ───────────────────────────────────────────────
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new ClienteYaExisteException(request.getEmail());
        }
        if (clienteRepository.existsByCedula(request.getNumeroCedula())) {
            throw new ClienteYaExisteException("cédula", request.getNumeroCedula());
        }

        // ── 2. Construir entidad de dominio ──────────────────────────────────
        // TODO Sprint 1 — Santiago: mapear request → dominio → persistencia → responseDto
        // Usar MapStruct para los mapeos (ver CODING_STANDARDS.md sección 3)

        // ── 3. Persistir ─────────────────────────────────────────────────────
        // Cliente clienteGuardado = clienteRepository.save(cliente);

        // ── 4. Retornar respuesta ────────────────────────────────────────────
        // return clienteMapper.toResponseDto(clienteGuardado);

        throw new UnsupportedOperationException("TODO: Implementar en Sprint 1 — HU1");
    }
}
