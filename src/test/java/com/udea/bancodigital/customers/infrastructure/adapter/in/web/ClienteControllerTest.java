package com.udea.bancodigital.customers.infrastructure.adapter.in.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.domain.port.in.ActualizarClientePort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.udea.bancodigital.auth.infrastructure.config.JwtAuthenticationFilter;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActualizarClientePort actualizarClientePort;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private com.udea.bancodigital.customers.domain.port.in.CrearClientePort crearClientePort;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deberiaRetornar400_siEmailEsInvalido() throws Exception {

        ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder()
                .email("correo-invalido")
                .build();

        mockMvc.perform(patch("/api/v1/clientes/{id}", UUID.randomUUID())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
