package com.udea.bancodigital.auth.infrastructure.adapter.in.web;

import com.udea.bancodigital.auth.application.dto.LoginRequestDto;
import com.udea.bancodigital.auth.application.dto.LoginResponseDto;
import com.udea.bancodigital.auth.application.dto.LogoutRequestDto;
import com.udea.bancodigital.auth.domain.port.in.AuthPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthPort authPort;

    public AuthController(AuthPort authPort) {
        this.authPort = authPort;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authPort.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        LogoutRequestDto request = new LogoutRequestDto();
        request.setToken(authorizationHeader);
        authPort.logout(request);
        return ResponseEntity.noContent().build();
    }
}
