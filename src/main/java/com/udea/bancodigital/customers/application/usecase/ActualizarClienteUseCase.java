package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.port.in.ActualizarClientePort;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActualizarClienteUseCase implements ActualizarClientePort {

    private final ClienteRepositoryPort repository;

    @Override
    @Transactional
    public ClienteResponseDto actualizarCliente(UUID id, ActualizarClienteRequestDto request) {

        // 1. Buscamos el cliente existente
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        // 2. Construcción del objeto de dominio con TODOS los campos
        // Se aplica lógica para actualizar solo si el campo viene en el DTO
        Cliente clienteActualizado = Cliente.builder()
                .id(cliente.getId()) // Mantiene ID original
                .numeroCedula(cliente.getNumeroCedula()) // Inmutable según HU3
                .fechaNacimiento(cliente.getFechaNacimiento()) // Inmutable según HU3

                // ACTUALIZACIÓN: Ahora sí toma el valor del DTO si no es nulo
                .activo(
                        request.getActivo() != null ? request.getActivo() : cliente.isActivo()
                )

                // Campos actualizables con validación de nulidad (PATCH parcial)
                .primerNombre(
                        request.getPrimerNombre() != null ? request.getPrimerNombre() : cliente.getPrimerNombre()
                )
                .segundoNombre(
                        request.getSegundoNombre() != null ? request.getSegundoNombre() : cliente.getSegundoNombre()
                )
                .primerApellido(
                        request.getPrimerApellido() != null ? request.getPrimerApellido() : cliente.getPrimerApellido()
                )
                .segundoApellido(
                        request.getSegundoApellido() != null ? request.getSegundoApellido() : cliente.getSegundoApellido()
                )
                .email(
                        request.getEmail() != null ? request.getEmail() : cliente.getEmail()
                )
                .telefono(
                        request.getTelefono() != null ? request.getTelefono() : cliente.getTelefono()
                )
                .build();

        // 3. Persistencia en el puerto de salida
        Cliente actualizado = repository.save(clienteActualizado);

        // 4. Mapeo completo al DTO de respuesta (Confirma el estado final al cliente)
        return ClienteResponseDto.builder()
                .id(actualizado.getId())
                .numeroCedula(actualizado.getNumeroCedula())
                .primerNombre(actualizado.getPrimerNombre())
                .segundoNombre(actualizado.getSegundoNombre())
                .primerApellido(actualizado.getPrimerApellido())
                .segundoApellido(actualizado.getSegundoApellido())
                .email(actualizado.getEmail())
                .telefono(actualizado.getTelefono())
                .fechaNacimiento(actualizado.getFechaNacimiento())
                .activo(actualizado.isActivo())
                .build();
    }
}