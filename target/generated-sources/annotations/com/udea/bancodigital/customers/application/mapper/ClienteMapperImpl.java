package com.udea.bancodigital.customers.application.mapper;

import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-14T11:36:33-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Arch Linux)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public Cliente toDomain(CrearClienteRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        Cliente.ClienteBuilder cliente = Cliente.builder();

        cliente.numeroCedula( stringToNumeroCedula( requestDto.getNumeroCedula() ) );
        cliente.email( stringToEmail( requestDto.getEmail() ) );
        cliente.primerNombre( requestDto.getPrimerNombre() );
        cliente.segundoNombre( requestDto.getSegundoNombre() );
        cliente.primerApellido( requestDto.getPrimerApellido() );
        cliente.segundoApellido( requestDto.getSegundoApellido() );
        cliente.telefono( requestDto.getTelefono() );
        cliente.fechaNacimiento( requestDto.getFechaNacimiento() );

        cliente.activo( true );

        return cliente.build();
    }

    @Override
    public ClienteEntity toEntity(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        ClienteEntity.ClienteEntityBuilder clienteEntity = ClienteEntity.builder();

        clienteEntity.numeroCedula( clienteNumeroCedulaValor( cliente ) );
        clienteEntity.email( clienteEmailValor( cliente ) );
        clienteEntity.id( cliente.getId() );
        clienteEntity.primerNombre( cliente.getPrimerNombre() );
        clienteEntity.segundoNombre( cliente.getSegundoNombre() );
        clienteEntity.primerApellido( cliente.getPrimerApellido() );
        clienteEntity.segundoApellido( cliente.getSegundoApellido() );
        clienteEntity.telefono( cliente.getTelefono() );
        clienteEntity.fechaNacimiento( cliente.getFechaNacimiento() );
        clienteEntity.activo( cliente.isActivo() );

        return clienteEntity.build();
    }

    @Override
    public Cliente toDomain(ClienteEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Cliente.ClienteBuilder cliente = Cliente.builder();

        cliente.numeroCedula( stringToNumeroCedula( entity.getNumeroCedula() ) );
        cliente.email( stringToEmail( entity.getEmail() ) );
        cliente.id( entity.getId() );
        cliente.primerNombre( entity.getPrimerNombre() );
        cliente.segundoNombre( entity.getSegundoNombre() );
        cliente.primerApellido( entity.getPrimerApellido() );
        cliente.segundoApellido( entity.getSegundoApellido() );
        cliente.telefono( entity.getTelefono() );
        cliente.fechaNacimiento( entity.getFechaNacimiento() );
        cliente.activo( entity.isActivo() );

        return cliente.build();
    }

    @Override
    public ClienteResponseDto toResponseDto(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        ClienteResponseDto.ClienteResponseDtoBuilder clienteResponseDto = ClienteResponseDto.builder();

        clienteResponseDto.numeroCedula( clienteNumeroCedulaValor( cliente ) );
        clienteResponseDto.email( clienteEmailValor( cliente ) );
        clienteResponseDto.id( cliente.getId() );
        clienteResponseDto.primerNombre( cliente.getPrimerNombre() );
        clienteResponseDto.segundoNombre( cliente.getSegundoNombre() );
        clienteResponseDto.primerApellido( cliente.getPrimerApellido() );
        clienteResponseDto.segundoApellido( cliente.getSegundoApellido() );
        clienteResponseDto.telefono( cliente.getTelefono() );
        clienteResponseDto.fechaNacimiento( cliente.getFechaNacimiento() );
        clienteResponseDto.activo( cliente.isActivo() );

        return clienteResponseDto.build();
    }

    @Override
    public void updateFromDto(ActualizarClienteRequestDto dto, Cliente cliente) {
        if ( dto == null ) {
            return;
        }
    }

    private String clienteNumeroCedulaValor(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }
        NumeroCedula numeroCedula = cliente.getNumeroCedula();
        if ( numeroCedula == null ) {
            return null;
        }
        String valor = numeroCedula.valor();
        if ( valor == null ) {
            return null;
        }
        return valor;
    }

    private String clienteEmailValor(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }
        Email email = cliente.getEmail();
        if ( email == null ) {
            return null;
        }
        String valor = email.valor();
        if ( valor == null ) {
            return null;
        }
        return valor;
    }
}
