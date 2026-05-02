package com.udea.bancodigital.accounts.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.bancodigital.accounts.application.dto.CrearCuentaRequestDto;
import com.udea.bancodigital.accounts.application.mapper.CuentaMapper;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;
import com.udea.bancodigital.accounts.domain.model.TipoCuenta;
import com.udea.bancodigital.accounts.domain.port.in.CrearCuentaPort;
import com.udea.bancodigital.accounts.domain.port.in.ConsultarSaldoPort;
import com.udea.bancodigital.accounts.domain.port.out.AuthServicePort;
import com.udea.bancodigital.auth.infrastructure.config.AuthJwtAuthenticationFilter;
import com.udea.bancodigital.infrastructure.config.SecurityConfig;
import com.udea.bancodigital.shared.security.AuthenticatedClientProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CuentaController.class)
@Import({SecurityConfig.class, CuentaMapper.class})
@ActiveProfiles("test")
class CuentaControllerTest {

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
    private AuthJwtAuthenticationFilter authJwtAuthenticationFilter;

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
    @WithMockUser(roles = "CAJERO")
    void deberiaCrearCuentaYRetornarApiResponse() throws Exception {
        UUID cuentaId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        CrearCuentaRequestDto request = CrearCuentaRequestDto.builder()
                .clienteId(clienteId)
                .tipoCuenta("AHORRO")
                .build();

        Cuenta cuenta = Cuenta.builder()
                .id(cuentaId)
                .numeroCuenta("CTA-1234567890ABCDEF")
                .clienteId(clienteId)
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldo(BigDecimal.ZERO)
                .estado(EstadoCuenta.ACTIVA)
                .fechaApertura(LocalDate.of(2026, 4, 1))
                .build();

        when(crearCuentaPort.crearCuenta(any(CrearCuentaRequestDto.class))).thenReturn(cuenta);

        mockMvc.perform(post("/api/v1/cuentas")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(cuentaId.toString()))
                .andExpect(jsonPath("$.data.numeroCuenta").value("CTA-1234567890ABCDEF"))
                .andExpect(jsonPath("$.data.tipoCuenta").value("AHORRO"))
                .andExpect(jsonPath("$.data.estado").value("ACTIVA"));
    }
}
