package com.udea.bancodigital.accounts.infrastructure.adapter.in.web;

import com.udea.bancodigital.accounts.application.dto.CrearCuentaRequestDto;
import com.udea.bancodigital.accounts.domain.port.in.CrearCuentaPort;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CrearCuentaPort crearCuentaPort;

    @PostMapping
    public ResponseEntity<Cuenta> crearCuenta(@Valid @RequestBody CrearCuentaRequestDto request) {
        Cuenta cuentaCreada = crearCuentaPort.crearCuenta(request);
        return new ResponseEntity<>(cuentaCreada, HttpStatus.CREATED);
    }
}
