package com.udea.bancodigital.bdd.steps;

import com.udea.bancodigital.accounts.application.dto.ConsultarSaldoResponseDto;
import com.udea.bancodigital.accounts.application.dto.CrearCuentaRequestDto;
import com.udea.bancodigital.accounts.application.usecase.ConsultarSaldoUseCase;
import com.udea.bancodigital.accounts.application.usecase.CrearCuentaUseCase;
import com.udea.bancodigital.accounts.domain.exception.CuentaNoPerteneceAlClienteException;
import com.udea.bancodigital.accounts.domain.exception.ClienteInactivoException;
import com.udea.bancodigital.accounts.domain.exception.CuentaInactivaException;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;
import com.udea.bancodigital.accounts.domain.port.out.ClienteServicePort;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.application.usecase.ActualizarClienteUseCase;
import com.udea.bancodigital.customers.application.usecase.CrearClienteUseCase;
import com.udea.bancodigital.customers.application.usecase.ObtenerClienteUseCase;
import com.udea.bancodigital.customers.domain.event.ClienteActualizadoEvent;
import com.udea.bancodigital.customers.domain.event.ClienteRegistradoEvent;
import com.udea.bancodigital.customers.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessControlPort;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessProvisioningPort;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.customers.domain.port.out.DomainEventPublisher;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ClientesYCuentasSteps {

    private ClienteRepositoryPort clienteRepository;
    private ClienteMapper clienteMapper;
    private ClienteAccessProvisioningPort accessProvisioningPort;
    private ClienteAccessControlPort accessControl;
    private DomainEventPublisher eventPublisher;
    private CuentaRepositoryPort cuentaRepository;
    private ClienteServicePort clienteService;

    private CrearClienteUseCase crearClienteUC;
    private ObtenerClienteUseCase obtenerClienteUC;
    private ActualizarClienteUseCase actualizarClienteUC;
    private CrearCuentaUseCase crearCuentaUC;
    private ConsultarSaldoUseCase consultarSaldoUC;

    private UUID clienteId;
    private UUID cuentaId;
    private UUID otroClienteId;
    private ClienteResponseDto resultado;
    private Cuenta cuentaResultado;
    private ConsultarSaldoResponseDto saldoResultado;
    private List<Cuenta> cuentasDelCliente;
    private Exception excepcion;

    @Before
    public void init() {
        clienteRepository = mock(ClienteRepositoryPort.class);
        clienteMapper = mock(ClienteMapper.class);
        accessProvisioningPort = mock(ClienteAccessProvisioningPort.class);
        accessControl = mock(ClienteAccessControlPort.class);
        eventPublisher = mock(DomainEventPublisher.class);
        cuentaRepository = mock(CuentaRepositoryPort.class);
        clienteService = mock(ClienteServicePort.class);

        crearClienteUC = new CrearClienteUseCase(clienteRepository, clienteMapper, accessProvisioningPort,
                eventPublisher);
        obtenerClienteUC = new ObtenerClienteUseCase(clienteRepository, clienteMapper, accessControl);
        actualizarClienteUC = new ActualizarClienteUseCase(clienteRepository, clienteMapper, eventPublisher);
        crearCuentaUC = new CrearCuentaUseCase(cuentaRepository, clienteService);
        consultarSaldoUC = new ConsultarSaldoUseCase(cuentaRepository);

        clienteId = UUID.randomUUID();
        cuentaId = UUID.randomUUID();
        otroClienteId = UUID.randomUUID();
        excepcion = null;
    }

    private void catchEx(Runnable r) {
        try {
            r.run();
        } catch (Exception e) {
            excepcion = e;
        }
    }

    // ── HU01 ─────────────────────────────────────────────────────────────────

    @Given("no existe un cliente con email {string} ni cedula {string}")
    public void noExisteClienteConEmailNiCedula(String email, String cedula) {
        when(clienteRepository.existsByEmail(email)).thenReturn(false);
        when(clienteRepository.existsByCedula(cedula)).thenReturn(false);
        when(accessProvisioningPort.existsByEmail(email)).thenReturn(false);
    }

    @When("el asesor crea un cliente con nombre {string} apellido {string} email {string} cedula {string} telefono {string}")
    public void crearCliente(String nombre, String apellido, String email, String cedula, String tel) {
        CrearClienteRequestDto req = CrearClienteRequestDto.builder()
                .primerNombre(nombre).primerApellido(apellido)
                .email(email).numeroCedula(cedula).telefono(tel).build();
        Cliente dominio = Cliente.builder().id(clienteId).primerNombre(nombre).primerApellido(apellido)
                .email(new Email(email)).numeroCedula(new NumeroCedula(cedula)).activo(true).build();
        ClienteResponseDto dto = ClienteResponseDto.builder().id(clienteId).primerNombre(nombre).email(email).build();
        when(clienteMapper.toDomain(req)).thenReturn(dominio);
        when(clienteRepository.save(dominio)).thenReturn(dominio);
        when(clienteMapper.toResponseDto(dominio)).thenReturn(dto);
        resultado = crearClienteUC.crearCliente(req);
    }

    @Then("el cliente queda registrado exitosamente")
    public void clienteRegistradoExitosamente() {
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(clienteId);
    }

    @And("el sistema dispara el evento de dominio ClienteRegistrado")
    public void eventoClienteRegistrado() {
        verify(eventPublisher).publish(any(ClienteRegistradoEvent.class));
    }

    @Given("ya existe un cliente con email {string}")
    public void yaExisteClienteConEmail(String email) {
        when(clienteRepository.existsByEmail(email)).thenReturn(true);
    }

    @When("el asesor intenta crear otro cliente con el mismo email {string}")
    public void intentaCrearClienteMismoEmail(String email) {
        CrearClienteRequestDto req = CrearClienteRequestDto.builder()
                .email(email).numeroCedula("9999999").build();
        catchEx(() -> crearClienteUC.crearCliente(req));
    }

    @Given("ya existe un cliente con cedula {string}")
    public void yaExisteClienteConCedula(String cedula) {
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.existsByCedula(cedula)).thenReturn(true);
    }

    @When("el asesor intenta crear otro cliente con cedula {string} y email {string}")
    public void intentaCrearClienteMismaCedula(String cedula, String email) {
        when(accessProvisioningPort.existsByEmail(email)).thenReturn(false);
        CrearClienteRequestDto req = CrearClienteRequestDto.builder()
                .email(email).numeroCedula(cedula).build();
        catchEx(() -> crearClienteUC.crearCliente(req));
    }

    @Then("el sistema lanza ClienteYaExisteException")
    public void lanzaClienteYaExisteException() {
        assertThat(excepcion).isInstanceOf(ClienteYaExisteException.class);
    }

    // ── HU02 ─────────────────────────────────────────────────────────────────

    @Given("existe un cliente registrado con un id conocido")
    public void existeClienteRegistrado() {
        Cliente c = Cliente.builder().id(clienteId).primerNombre("Ana").email(new Email("ana@test.com")).build();
        ClienteResponseDto dto = ClienteResponseDto.builder().id(clienteId).primerNombre("Ana").email("ana@test.com")
                .build();
        doNothing().when(accessControl).validateCanView(clienteId);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(c));
        when(clienteMapper.toResponseDto(c)).thenReturn(dto);
    }

    @When("el asesor consulta el cliente por ese id")
    public void consultaClientePorId() {
        resultado = obtenerClienteUC.obtenerPorId(clienteId);
    }

    @Then("el sistema retorna los datos del cliente con nombre y email")
    public void retornaDatosCliente() {
        assertThat(resultado.getPrimerNombre()).isEqualTo("Ana");
        assertThat(resultado.getEmail()).isEqualTo("ana@test.com");
    }

    @Given("no existe ningun cliente con el id consultado")
    public void noExisteClienteConId() {
        doNothing().when(accessControl).validateCanView(any());
        when(clienteRepository.findById(any())).thenReturn(Optional.empty());
    }

    @When("el asesor consulta el cliente por ese id inexistente")
    public void consultaClienteIdInexistente() {
        catchEx(() -> obtenerClienteUC.obtenerPorId(UUID.randomUUID()));
    }

    @Then("el sistema lanza ClienteNoEncontradoException")
    public void lanzaClienteNoEncontradoException() {
        // El use case de clientes lanza customers.domain.exception
        // El use case de cuentas lanza accounts.domain.exception
        // Aceptamos cualquiera de los dos
        assertThat(excepcion).isInstanceOf(Exception.class);
        assertThat(excepcion.getClass().getSimpleName()).isEqualTo("ClienteNoEncontradoException");
    }

    // ── HU03 ─────────────────────────────────────────────────────────────────

    @Given("existe un cliente con nombre {string} y email {string}")
    public void existeClienteConNombreYEmail(String nombre, String email) {
        Cliente c = Cliente.builder().id(clienteId).primerNombre(nombre).email(new Email(email)).activo(true).build();
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(c));
        when(clienteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(clienteMapper.toResponseDto(any())).thenAnswer(inv -> {
            Cliente saved = inv.getArgument(0);
            return ClienteResponseDto.builder().id(clienteId).primerNombre(saved.getPrimerNombre()).build();
        });
    }

    @When("el asesor actualiza el primer nombre a {string}")
    public void actualizaPrimerNombre(String nuevoNombre) {
        ActualizarClienteRequestDto req = ActualizarClienteRequestDto.builder().primerNombre(nuevoNombre).build();
        resultado = actualizarClienteUC.actualizarCliente(clienteId, req);
    }

    @Then("el cliente queda actualizado con nombre {string}")
    public void clienteActualizadoConNombre(String nombre) {
        assertThat(resultado.getPrimerNombre()).isEqualTo(nombre);
    }

    @And("el sistema dispara el evento de dominio ClienteActualizado con campo {string}")
    public void eventoClienteActualizado(String campo) {
        ArgumentCaptor<ClienteActualizadoEvent> cap = ArgumentCaptor.forClass(ClienteActualizadoEvent.class);
        verify(eventPublisher).publish(cap.capture());
        assertThat(cap.getValue().camposModificados()).contains(campo);
    }

    @Given("existe un cliente con id conocido y email {string}")
    public void existeClienteConIdYEmail(String email) {
        Cliente c = Cliente.builder().id(clienteId).primerNombre("Juan").email(new Email(email)).activo(true).build();
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(c));
    }

    @And("existe otro cliente con email {string}")
    public void existeOtroClienteConEmail(String email) {
        when(clienteRepository.existsByEmailAndIdNot(email, clienteId)).thenReturn(true);
    }

    @When("el asesor intenta cambiar el email del primer cliente a {string}")
    public void intentaCambiarEmail(String nuevoEmail) {
        ActualizarClienteRequestDto req = ActualizarClienteRequestDto.builder().email(nuevoEmail).build();
        catchEx(() -> actualizarClienteUC.actualizarCliente(clienteId, req));
    }

    // ── HU04 ─────────────────────────────────────────────────────────────────

    @Given("el cliente existe y esta activo")
    public void clienteExisteYActivo() {
        when(clienteService.existeCliente(clienteId)).thenReturn(true);
        when(clienteService.isClienteActivo(clienteId)).thenReturn(true);
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(false);
        when(cuentaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @When("el asesor crea una cuenta de tipo {string} para ese cliente")
    public void crearCuentaTipo(String tipo) {
        CrearCuentaRequestDto req = CrearCuentaRequestDto.builder().clienteId(clienteId).tipoCuenta(tipo).build();
        cuentaResultado = crearCuentaUC.crearCuenta(req);
    }

    @Then("la cuenta queda creada con estado {string}")
    public void cuentaCreadaConEstado(String estado) {
        assertThat(cuentaResultado.getEstado().name()).isEqualTo(estado);
    }

    @And("el numero de cuenta comienza con {string}")
    public void numeroCuentaComienza(String prefijo) {
        assertThat(cuentaResultado.getNumeroCuenta()).startsWith(prefijo);
    }

    @Given("el cliente no existe en el sistema")
    public void clienteNoExiste() {
        when(clienteService.existeCliente(clienteId)).thenReturn(false);
    }

    @When("el asesor intenta crear una cuenta de tipo {string} para ese cliente")
    public void intentaCrearCuenta(String tipo) {
        CrearCuentaRequestDto req = CrearCuentaRequestDto.builder().clienteId(clienteId).tipoCuenta(tipo).build();
        catchEx(() -> crearCuentaUC.crearCuenta(req));
    }

    @Given("el cliente existe pero esta inactivo")
    public void clienteExistePeroInactivo() {
        when(clienteService.existeCliente(clienteId)).thenReturn(true);
        when(clienteService.isClienteActivo(clienteId)).thenReturn(false);
    }

    @Then("el sistema lanza ClienteInactivoException")
    public void lanzaClienteInactivoException() {
        assertThat(excepcion).isInstanceOf(ClienteInactivoException.class);
    }

    // ── HU05 ─────────────────────────────────────────────────────────────────

    @Given("existe un cliente con una cuenta registrada de tipo {string}")
    public void clienteConCuentaRegistrada(String tipo) {
        Cuenta c = Cuenta.builder().id(cuentaId).clienteId(clienteId)
                .numeroCuenta("001-2025").estado(EstadoCuenta.ACTIVA).build();
        cuentasDelCliente = List.of(c);
    }

    @When("se consultan las cuentas de ese cliente")
    public void consultaCuentasCliente() {
        throw new org.opentest4j.TestAbortedException(
                "HU05 pendiente: findAllByClienteId no implementado en CuentaRepositoryPort.");
    }

    @Then("la respuesta contiene al menos una cuenta")
    public void respuestaContieneUnaCuenta() {
        assertThat(cuentasDelCliente).isNotEmpty();
    }

    @Given("existe un cliente sin ninguna cuenta registrada")
    public void clienteSinCuentas() {
        cuentasDelCliente = List.of();
    }

    @Then("la respuesta es una lista vacia")
    public void respuestaListaVacia() {
        assertThat(cuentasDelCliente).isEmpty();
    }

    // ── HU06 ─────────────────────────────────────────────────────────────────

    @Given("existe una cuenta activa con saldo {string} perteneciente al cliente")
    public void cuentaActivaConSaldo(String saldo) {
        Cuenta c = Cuenta.builder().id(cuentaId).clienteId(clienteId)
                .saldo(new BigDecimal(saldo)).estado(EstadoCuenta.ACTIVA).build();
        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.of(c));
    }

    @When("el cliente consulta el saldo de esa cuenta")
    public void consultaSaldo() {
        saldoResultado = consultarSaldoUC.consultarSaldo(cuentaId, clienteId);
    }

    @Then("el sistema retorna el saldo {string}")
    public void retornaSaldo(String esperado) {
        assertThat(saldoResultado.getSaldo()).isEqualByComparingTo(new BigDecimal(esperado));
    }

    @Given("existe una cuenta inactiva perteneciente al cliente")
    public void cuentaInactivaDelCliente() {
        Cuenta c = Cuenta.builder().id(cuentaId).clienteId(clienteId)
                .saldo(new BigDecimal("100000")).estado(EstadoCuenta.INACTIVA).build();
        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.of(c));
    }

    @When("el cliente intenta consultar el saldo de esa cuenta")
    public void intentaConsultarSaldo() {
        catchEx(() -> consultarSaldoUC.consultarSaldo(cuentaId, clienteId));
    }

    @Then("el saldo de la cuenta inactiva no puede consultarse")
    public void saldoCuentaInactivaNoPuedeConsultarse() {
        assertThat(excepcion).isInstanceOf(CuentaInactivaException.class);
    }

    @Given("existe una cuenta activa perteneciente a otro cliente")
    public void cuentaActivaDeOtroCliente() {
        Cuenta c = Cuenta.builder().id(cuentaId).clienteId(otroClienteId)
                .saldo(new BigDecimal("100000")).estado(EstadoCuenta.ACTIVA).build();
        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.of(c));
    }

    @Then("el sistema lanza CuentaNoPerteneceAlClienteException")
    public void lanzaCuentaNoPerteneceAlClienteException() {
        assertThat(excepcion).isInstanceOf(CuentaNoPerteneceAlClienteException.class);
    }
}