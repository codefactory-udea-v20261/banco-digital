package com.udea.bancodigital.customers.infrastructure.adapter.in.web;
import com.udea.bancodigital.infrastructure.security.JwtAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;

import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.domain.port.in.ActualizarClientePort;
import com.udea.bancodigital.customers.domain.port.in.CrearClientePort;
import com.udea.bancodigital.customers.domain.port.in.ObtenerClientePort;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessControlPort;
import com.udea.bancodigital.infrastructure.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ClienteControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrearClientePort crearClientePort;

    @MockBean
    private ActualizarClientePort actualizarClientePort;

    @MockBean
    private ObtenerClientePort obtenerClientePort;

    @MockBean
    private ClienteAccessControlPort clienteAccessControlPort;

    @MockBean
    private com.udea.bancodigital.accounts.domain.port.in.ListarCuentasClientePort listarCuentasClientePort;

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
    @WithMockUser(authorities = "PERM_READ_OWN_PROFILE")
    void deberiaRetornar403_siUsuarioNoEsAsesorNiAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "PERM_MANAGE_CLIENTS")
    void deberiaPermitirRegistro_siUsuarioEsAsesor() throws Exception {
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "PERM_READ_OWN_PROFILE")
    void deberiaRetornar403_siClienteIntentaActualizarPerfil() throws Exception {
        mockMvc.perform(patch("/api/v1/clientes/{id}", UUID.randomUUID())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(actualizacionValida())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "PERM_MANAGE_CLIENTS")
    void deberiaPermitirActualizacion_siUsuarioEsAsesor() throws Exception {
        UUID id = UUID.randomUUID();
        when(actualizarClientePort.actualizarCliente(eq(id), any(ActualizarClienteRequestDto.class)))
                .thenReturn(ClienteResponseDto.builder()
                        .id(id)
                        .numeroCedula("1234567890")
                        .primerNombre("Laura")
                        .primerApellido("Lopez")
                        .email("laura@test.com")
                        .activo(true)
                        .createdAt(Instant.now())
                        .build());

        mockMvc.perform(patch("/api/v1/clientes/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(actualizacionValida())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "PERM_MANAGE_CLIENTS")
    void deberiaPermitirConsultaSiUsuarioEsAsesor() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(clienteAccessControlPort).validateCanView(id);
        when(clienteAccessControlPort.canManageClientes()).thenReturn(true);
        when(obtenerClientePort.obtenerPorId(id)).thenReturn(ClienteResponseDto.builder()
                .id(id)
                .numeroCedula("1234567890")
                .primerNombre("Laura")
                .primerApellido("Lopez")
                .email("laura@test.com")
                .activo(true)
                .createdAt(Instant.now())
                .build());

        mockMvc.perform(get("/api/v1/clientes/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "PERM_VIEW_AUDIT")
    void deberiaRetornar403SiAuditorIntentaConsultarCliente() throws Exception {
        mockMvc.perform(get("/api/v1/clientes/{id}", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "PERM_READ_OWN_PROFILE")
    void deberiaPermitirListarCuentasConPermisoLectura() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(clienteAccessControlPort).validateCanView(id);
        when(listarCuentasClientePort.listarPorClienteId(id)).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/clientes/{id}/cuentas", id))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar401SiNoAutenticadoAlListarCuentas() throws Exception {
        mockMvc.perform(get("/api/v1/clientes/{id}/cuentas", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    private CrearClienteRequestDto requestValido() {
        return CrearClienteRequestDto.builder()
                .numeroCedula("1234567890")
                .primerNombre("Laura")
                .primerApellido("Lopez")
                .email("laura@test.com")
                .telefono("3001234567")
                .fechaNacimiento(LocalDate.of(1995, 6, 10))
                .build();
    }

    private ActualizarClienteRequestDto actualizacionValida() {
        return ActualizarClienteRequestDto.builder()
                .primerNombre("Laura")
                .telefono("3001234567")
                .build();
    }
}
