package com.udea.bancodigital.accounts.infrastructure.adapter.in.web;
import com.udea.bancodigital.infrastructure.security.JwtAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.bancodigital.accounts.application.dto.CrearCuentaRequestDto;
import com.udea.bancodigital.accounts.application.mapper.CuentaMapper;
import com.udea.bancodigital.accounts.domain.port.in.CrearCuentaPort;
import com.udea.bancodigital.accounts.domain.port.in.ConsultarSaldoPort;
import com.udea.bancodigital.accounts.domain.port.out.AuthServicePort;

import com.udea.bancodigital.infrastructure.config.SecurityConfig;
import com.udea.bancodigital.shared.security.AuthenticatedClientProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.udea.bancodigital.accounts.application.dto.ConsultarSaldoResponseDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CuentaController.class)
@Import({SecurityConfig.class, CuentaMapper.class})
@ActiveProfiles("test")
class CuentaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrearCuentaPort crearCuentaPort;

    @MockBean
    private ConsultarSaldoPort consultarSaldoPort;

    @MockBean
    private AuthServicePort authServicePort;

    @MockBean
    private AuthenticatedClientProvider authenticatedClientProvider;

    @MockBean
    private JwtAuthenticationFilter authJwtAuthenticationFilter;

    @MockBean
    private com.udea.bancodigital.infrastructure.security.IdentityServiceClient identityServiceClient;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(
                    invocation.getArgument(0, ServletRequest.class),
                    invocation.getArgument(1, ServletResponse.class)
            );
            return null;
        }).when(authJwtAuthenticationFilter).doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
    }

    @Test
    @WithMockUser(authorities = "PERM_READ_OWN_BALANCE")
    void deberiaRetornar403SiUsuarioNoEsAsesorNiAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/cuentas")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "PERM_CREATE_ACCOUNTS")
    void deberiaPermitirCreacionSiUsuarioEsAsesor() throws Exception {
        when(crearCuentaPort.crearCuenta(any(CrearCuentaRequestDto.class))).thenReturn(
                com.udea.bancodigital.accounts.domain.model.Cuenta.builder()
                        .id(UUID.randomUUID())
                        .numeroCuenta("CTA-1234567890ABCDEF")
                        .clienteId(UUID.randomUUID())
                        .tipoCuenta(com.udea.bancodigital.accounts.domain.model.TipoCuenta.AHORRO)
                        .saldo(BigDecimal.ZERO)
                        .estado(com.udea.bancodigital.accounts.domain.model.EstadoCuenta.ACTIVA)
                        .fechaApertura(LocalDate.of(2026, 4, 1))
                        .build()
        );

        mockMvc.perform(post("/api/v1/cuentas")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "PERM_READ_OWN_BALANCE")
    void deberiaPermitirConsultarSaldoPropio() throws Exception {
        UUID cuentaId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        
        when(authenticatedClientProvider.getClienteId()).thenReturn(clienteId);
        when(consultarSaldoPort.consultarSaldo(cuentaId, clienteId))
                .thenReturn(ConsultarSaldoResponseDto.builder()
                        .saldo(new BigDecimal("50000.00"))
                        .build());

        mockMvc.perform(get("/api/v1/cuentas/{id}/saldo", cuentaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.saldo").value(50000.00));
    }

    @Test
    @WithMockUser(authorities = "PERM_CREATE_ACCOUNTS")
    void cajeroDebePoderConsultarCualquierSaldo() throws Exception {
        UUID cuentaId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        
        when(authenticatedClientProvider.getClienteId()).thenReturn(clienteId);
        when(consultarSaldoPort.consultarSaldo(cuentaId, clienteId))
                .thenReturn(ConsultarSaldoResponseDto.builder()
                        .saldo(new BigDecimal("100000.00"))
                        .build());

        mockMvc.perform(get("/api/v1/cuentas/{id}/saldo", cuentaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(authorities = {"PERM_CREATE_ACCOUNTS", "PERM_READ_OWN_BALANCE"})
    void adminDebePoderConsultarCualquierSaldo() throws Exception {
        UUID cuentaId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        
        when(authenticatedClientProvider.getClienteId()).thenReturn(clienteId);
        when(consultarSaldoPort.consultarSaldo(cuentaId, clienteId))
                .thenReturn(ConsultarSaldoResponseDto.builder()
                        .saldo(new BigDecimal("200000.00"))
                        .build());

        mockMvc.perform(get("/api/v1/cuentas/{id}/saldo", cuentaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private CrearCuentaRequestDto requestValido() {
        return CrearCuentaRequestDto.builder()
                .clienteId(UUID.randomUUID())
                .tipoCuenta("AHORRO")
                .build();
    }
}
