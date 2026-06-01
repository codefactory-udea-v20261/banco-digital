package com.udea.bancodigital.customers.infrastructure.adapter.in.web;
import com.udea.bancodigital.infrastructure.security.JwtAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.domain.exception.ClienteNoAutorizadoException;
import com.udea.bancodigital.customers.domain.port.in.ActualizarClientePort;
import com.udea.bancodigital.customers.domain.port.in.ObtenerClientePort;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessControlPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActualizarClientePort actualizarClientePort;

    @MockBean
    private JwtAuthenticationFilter authJwtAuthenticationFilter;

    @MockBean
    private com.udea.bancodigital.infrastructure.security.IdentityServiceClient identityServiceClient;

    @MockBean
    private com.udea.bancodigital.customers.domain.port.in.CrearClientePort crearClientePort;

    @MockBean
    private ObtenerClientePort obtenerClientePort;

    @MockBean
    private ClienteAccessControlPort clienteAccessControlPort;

    @MockBean
    private com.udea.bancodigital.accounts.domain.port.in.ListarCuentasClientePort listarCuentasClientePort;

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

    @Test
    void deberiaRetornarClienteConLinksHateoas() throws Exception {
        UUID id = UUID.randomUUID();
        ClienteResponseDto response = ClienteResponseDto.builder()
                .id(id)
                .numeroCedula("1234567890")
                .primerNombre("Maria")
                .primerApellido("Gonzalez")
                .email("maria@test.com")
                .activo(true)
                .createdAt(Instant.now())
                .build();

        doNothing().when(clienteAccessControlPort).validateCanView(id);
        when(clienteAccessControlPort.canManageClientes()).thenReturn(true);
        when(obtenerClientePort.obtenerPorId(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.links[0].href", endsWith("/api/v1/clientes/" + id)))
                .andExpect(jsonPath("$.data.links[1].rel").value("actualizar"))
                .andExpect(jsonPath("$.data.links[1].href", endsWith("/api/v1/clientes/" + id)));
    }

    @Test
    void deberiaRetornar403SiNoTienePermisoParaConsultarCliente() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ClienteNoAutorizadoException(id)).when(clienteAccessControlPort).validateCanView(id);

        mockMvc.perform(get("/api/v1/clientes/{id}", id))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("CLIENTE_NO_AUTORIZADO"));
    }
}
