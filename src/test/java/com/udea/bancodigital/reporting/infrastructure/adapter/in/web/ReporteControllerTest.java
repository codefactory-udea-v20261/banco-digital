package com.udea.bancodigital.reporting.infrastructure.adapter.in.web;

import com.udea.bancodigital.auth.infrastructure.config.JwtAuthenticationFilter;
import com.udea.bancodigital.infrastructure.config.SecurityConfig;
import com.udea.bancodigital.reporting.application.dto.SaldoTotalClienteResponseDto;
import com.udea.bancodigital.reporting.domain.port.in.ConsultarSaldoTotalClientePort;
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
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReporteController.class)
@Import(SecurityConfig.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultarSaldoTotalClientePort consultarSaldoTotalClientePort;

    @MockBean
    private AuthenticatedClientProvider authenticatedClientProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(
                    invocation.getArgument(0, ServletRequest.class),
                    invocation.getArgument(1, ServletResponse.class)
            );
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deberiaRetornarSaldoTotalDesdeApiDeReportes() throws Exception {
        UUID clienteId = UUID.randomUUID();
        when(authenticatedClientProvider.getClienteId()).thenReturn(clienteId);
        when(consultarSaldoTotalClientePort.consultarSaldoTotal(eq(clienteId)))
                .thenReturn(SaldoTotalClienteResponseDto.builder()
                        .clienteId(clienteId)
                        .saldoTotal(new BigDecimal("150000.00"))
                        .build());

        mockMvc.perform(get("/api/v1/reportes/saldo-total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.clienteId").value(clienteId.toString()))
                .andExpect(jsonPath("$.data.saldoTotal").value(150000.00));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deberiaRetornar501EnEndpointsReservadosDeReportes() throws Exception {
        mockMvc.perform(get("/api/v1/reportes/movimientos"))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("NOT_IMPLEMENTED"));
    }
}
