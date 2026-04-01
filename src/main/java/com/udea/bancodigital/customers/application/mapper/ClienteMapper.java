package com.udea.bancodigital.customers.application.mapper;

import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper centralizado para conversiones entre capas del módulo customers.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClienteMapper {

    /**
     * Convierte CrearClienteRequestDto → Cliente (dominio).
     * Usado en CrearClienteUseCase.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "numeroCedula", source = "numeroCedula")
    @Mapping(target = "email", source = "email")
    Cliente toDomain(CrearClienteRequestDto requestDto);

    /**
     * Convierte Cliente (dominio) → ClienteEntity (JPA).
     * Usado en ClienteRepositoryAdapter.save()
     */
    @Mapping(target = "numeroCedula", source = "numeroCedula.valor")
    @Mapping(target = "email", source = "email.valor")
    ClienteEntity toEntity(Cliente cliente);

    /**
     * Convierte ClienteEntity (JPA) → Cliente (dominio).
     * Usado en ClienteRepositoryAdapter al leer de BD.
     */
    @Mapping(target = "numeroCedula", source = "numeroCedula")
    @Mapping(target = "email", source = "email")
    Cliente toDomain(ClienteEntity entity);

    /**
     * Convierte Cliente (dominio) → ClienteResponseDto.
     * Usado en todos los casos de uso para respuestas.
     */
    @Mapping(target = "numeroCedula", source = "numeroCedula.valor")
    @Mapping(target = "email", source = "email.valor")
    ClienteResponseDto toResponseDto(Cliente cliente);

    /**
     * Reemplaza el builder manual en ActualizarClienteUseCase.
     * Solo actualiza los campos NO nulos del DTO (NullValuePropertyMappingStrategy.IGNORE).
     * 
     * @param dto Datos a actualizar (solo campos no nulos)
     * @param cliente Entidad existente que será modificada
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroCedula", ignore = true)
    @Mapping(target = "fechaNacimiento", ignore = true)
    void updateFromDto(ActualizarClienteRequestDto dto, @MappingTarget Cliente cliente);

    /**
     * Convierte String → NumeroCedula.
     * MapStruct usa este método automáticamente cuando encuentra la conversión.
     */
    default NumeroCedula stringToNumeroCedula(String valor) {
        return valor != null ? NumeroCedula.of(valor) : null;
    }

    /**
     * Convierte NumeroCedula → String.
     * Para conversión Entity/DTO.
     */
    default String numeroCedulaToString(NumeroCedula numeroCedula) {
        return numeroCedula != null ? numeroCedula.valor() : null;
    }

    /**
     * Convierte String → Email.
     * MapStruct usa este método automáticamente.
     */
    default Email stringToEmail(String valor) {
        return valor != null ? Email.of(valor) : null;
    }

    /**
     * Convierte Email → String.
     * Para conversión Entity/DTO.
     */
    default String emailToString(Email email) {
        return email != null ? email.valor() : null;
    }
}
