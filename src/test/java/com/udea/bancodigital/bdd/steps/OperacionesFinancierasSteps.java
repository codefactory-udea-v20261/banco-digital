package com.udea.bancodigital.bdd.steps;

import com.udea.bancodigital.accounts.domain.exception.CuentaInactivaException;
import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import com.udea.bancodigital.accounts.infrastructure.repository.CuentaJpaRepository;
import com.udea.bancodigital.transactions.application.dto.HistorialTransaccionDto;
import com.udea.bancodigital.transactions.application.dto.RetiroRequestDto;
import com.udea.bancodigital.transactions.application.dto.TransferenciaRequestDto;
import com.udea.bancodigital.transactions.application.dto.TransferenciaResponseDto;
import com.udea.bancodigital.transactions.application.mapper.HistorialTransaccionMapper;
import com.udea.bancodigital.transactions.application.usecase.ConsultarHistorialUseCase;
import com.udea.bancodigital.transactions.application.usecase.RealizarRetiroUseCase;
import com.udea.bancodigital.transactions.application.usecase.TransferirDineroUseCase;
import com.udea.bancodigital.transactions.domain.enums.EstadoTransaccion;
import com.udea.bancodigital.transactions.domain.exception.CuentaTransaccionException;
import com.udea.bancodigital.transactions.domain.exception.SaldoInsuficienteException;
import com.udea.bancodigital.transactions.domain.exception.TransferenciaInvalidaException;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.domain.port.out.CuentaServicePort;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
 
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
 
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OperacionesFinancierasSteps{

    private final ScenarioContext ctx;
 
    private CuentaJpaRepository        cuentaJpaRepo;
    private TransaccionRepositoryPort  transaccionRepo;
    private CuentaServicePort          cuentaService;
    private HistorialTransaccionMapper historialMapper;
 
    private TransferirDineroUseCase   transferirUC;
    private RealizarRetiroUseCase     retiroUC;
    private ConsultarHistorialUseCase historialUC;
 
    private UUID                          cuentaId;
    private TransferenciaResponseDto      transferenciaResult;
    private List<HistorialTransaccionDto> historialResult;
    private CuentaEntity                  cuentaOrigen;
    private CuentaEntity                  cuentaDestino;
 
    public OperacionesFinancierasSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }
 
    @Before
    public void init() {
        cuentaJpaRepo   = mock(CuentaJpaRepository.class);
        transaccionRepo = mock(TransaccionRepositoryPort.class);
        cuentaService   = mock(CuentaServicePort.class);
        historialMapper = mock(HistorialTransaccionMapper.class);
 
        transferirUC = new TransferirDineroUseCase(cuentaJpaRepo, transaccionRepo);
        retiroUC     = new RealizarRetiroUseCase(transaccionRepo, cuentaService);
        historialUC  = new ConsultarHistorialUseCase(transaccionRepo, historialMapper);
 
        cuentaId = UUID.randomUUID();
        ctx.reset();
    }
 
    private CuentaEntity cuentaEntity(String numero, String estado, String saldo) {
        CuentaEntity c = new CuentaEntity();
        c.setId(UUID.randomUUID());
        c.setNumeroCuenta(numero);
        c.setEstado(estado);
        c.setSaldo(new BigDecimal(saldo));
        return c;
    }
 
    private void catchEx(Runnable r) {
        try { r.run(); } catch (Exception e) { ctx.setExcepcion(e); }
    }
 
    // ── HU07 ─────────────────────────────────────────────────────────────────
 
    @Given("la cuenta origen {string} esta activa con saldo {string}")
    public void cuentaOrigenActivaConSaldo(String numero, String saldo) {
        cuentaOrigen = cuentaEntity(numero, "ACTIVA", saldo);
        when(cuentaJpaRepo.findByNumeroCuenta(numero)).thenReturn(Optional.of(cuentaOrigen));
    }
 
    @And("la cuenta destino {string} esta activa con saldo {string}")
    public void cuentaDestinoActivaConSaldo(String numero, String saldo) {
        cuentaDestino = cuentaEntity(numero, "ACTIVA", saldo);
        when(cuentaJpaRepo.findByNumeroCuenta(numero)).thenReturn(Optional.of(cuentaDestino));
    }
 
    @And("la cuenta destino {string} esta activa")
    public void cuentaDestinoActiva(String numero) {
        cuentaDestino = cuentaEntity(numero, "ACTIVA", "0");
        when(cuentaJpaRepo.findByNumeroCuenta(numero)).thenReturn(Optional.of(cuentaDestino));
    }
 
    @When("se transfieren {string} de la cuenta {string} a la cuenta {string}")
    public void seTransfieren(String monto, String origen, String destino) {
        Transaccion tx = Transaccion.builder().id(UUID.randomUUID())
                .estado(EstadoTransaccion.COMPLETADA).build();
        when(transaccionRepo.save(any())).thenReturn(tx);
        TransferenciaRequestDto req = new TransferenciaRequestDto(origen, destino, new BigDecimal(monto));
        transferenciaResult = transferirUC.transferir(req, "user1");
    }
 
    @Then("el saldo de la cuenta {string} queda en {string}")
    public void saldoCuentaQuedaEn(String numero, String saldo) {
        if (cuentaOrigen != null && cuentaOrigen.getNumeroCuenta().equals(numero))
            assertThat(cuentaOrigen.getSaldo()).isEqualByComparingTo(new BigDecimal(saldo));
        else if (cuentaDestino != null && cuentaDestino.getNumeroCuenta().equals(numero))
            assertThat(cuentaDestino.getSaldo()).isEqualByComparingTo(new BigDecimal(saldo));
    }
 
    @And("se guardan exactamente {int} transacciones en el repositorio")
    public void seGuardanTransacciones(int cantidad) {
        verify(transaccionRepo, times(cantidad)).save(any(Transaccion.class));
    }
 
    @When("se intenta transferir {string} de la cuenta {string} a la cuenta {string}")
    public void seIntentaTransferir(String monto, String origen, String destino) {
        TransferenciaRequestDto req = new TransferenciaRequestDto(origen, destino, new BigDecimal(monto));
        catchEx(() -> transferirUC.transferir(req, "user1"));
    }
 
    @Then("el sistema lanza SaldoInsuficienteException")
    public void lanzaSaldoInsuficienteException() {
        assertThat(ctx.getExcepcion()).isInstanceOf(SaldoInsuficienteException.class);
    }
 
    @And("no se guarda ninguna transaccion ni se modifica ningun saldo")
    public void noSeGuardaTransaccion() {
        verify(transaccionRepo, never()).save(any());
        verify(cuentaJpaRepo,   never()).save(any());
    }
 
    @And("la cuenta destino {string} no existe en el sistema")
    public void cuentaDestinoNoExiste(String numero) {
        when(cuentaJpaRepo.findByNumeroCuenta(numero)).thenReturn(Optional.empty());
    }
 
    @Then("el sistema lanza TransferenciaInvalidaException con mensaje {string}")
    public void lanzaTransferenciaInvalidaConMensaje(String fragmento) {
        assertThat(ctx.getExcepcion()).isInstanceOf(TransferenciaInvalidaException.class)
                .hasMessageContaining(fragmento);
    }
 
    @Given("la cuenta origen {string} esta en estado {string}")
    public void cuentaOrigenEnEstado(String numero, String estado) {
        cuentaOrigen = cuentaEntity(numero, estado, "100.0");
        when(cuentaJpaRepo.findByNumeroCuenta(numero)).thenReturn(Optional.of(cuentaOrigen));
    }
 
    @And("la cuenta destino {string} esta en estado {string}")
    public void cuentaDestinoEnEstado(String numero, String estado) {
        cuentaDestino = cuentaEntity(numero, estado, "0");
        when(cuentaJpaRepo.findByNumeroCuenta(numero)).thenReturn(Optional.of(cuentaDestino));
    }
 
    @And("la cuenta destino {string} existe en el sistema")
    public void cuentaDestinoExiste(String numero) {
        cuentaDestino = cuentaEntity(numero, "ACTIVA", "0");
        when(cuentaJpaRepo.findByNumeroCuenta(numero)).thenReturn(Optional.of(cuentaDestino));
    }
 
    @Then("el sistema lanza CuentaInactivaException")
    public void lanzaCuentaInactivaException() {
        assertThat(ctx.getExcepcion()).isInstanceOf(CuentaInactivaException.class);
    }
 
    @When("se intenta transferir {string} de la cuenta {string} a la misma cuenta {string}")
    public void seIntentaTransferirMismaCuenta(String monto, String c1, String c2) {
        TransferenciaRequestDto req = new TransferenciaRequestDto(c1, c2, new BigDecimal(monto));
        catchEx(() -> transferirUC.transferir(req, "user1"));
    }
 
    // ── HU08 ─────────────────────────────────────────────────────────────────
 
    @Given("la cuenta tiene saldo disponible de {string}")
    public void cuentaTieneSaldoDisponible(String saldo) {
        when(cuentaService.consultarSaldo(any())).thenReturn(Optional.of(new BigDecimal(saldo)));
    }
 
    @When("el cliente solicita un retiro de {string}")
    public void clienteSolicitaRetiro(String monto) {
        Transaccion tx = Transaccion.builder().id(UUID.randomUUID())
                .referencia("RET-" + System.currentTimeMillis()).build();
        when(transaccionRepo.save(any())).thenReturn(tx);
        RetiroRequestDto req = new RetiroRequestDto(cuentaId, new BigDecimal(monto), "Retiro BDD");
        catchEx(() -> {
            Transaccion result = retiroUC.ejecutar(req);
            assertThat(result.getReferencia()).isNotNull();
            assertThat(result.getReferencia().length()).isLessThanOrEqualTo(50);
        });
    }
 
    @Then("el saldo se actualiza restando {string}")
    public void saldoActualizadoRestando(String monto) {
        verify(cuentaService).actualizarSaldo(any(), eq(new BigDecimal(monto)));
    }
 
    @And("la transaccion queda registrada con una referencia no nula de maximo 50 caracteres")
    public void transaccionConReferencia() {
        assertThat(ctx.getExcepcion()).isNull();
    }
 
    @Given("la cuenta no existe en el sistema de retiro")
    public void cuentaNoExisteEnSistemaRetiro() {
        when(cuentaService.consultarSaldo(any())).thenReturn(Optional.empty());
    }
 
    @When("el cliente intenta realizar un retiro de {string}")
    public void clienteIntentaRetiro(String monto) {
        RetiroRequestDto req = new RetiroRequestDto(cuentaId, new BigDecimal(monto), "Retiro BDD");
        catchEx(() -> retiroUC.ejecutar(req));
    }
 
    @Then("el sistema lanza CuentaTransaccionException")
    public void lanzaCuentaTransaccionException() {
        assertThat(ctx.getExcepcion()).isInstanceOf(CuentaTransaccionException.class);
    }
 
    // ── HU09 ─────────────────────────────────────────────────────────────────
 
    @Given("la cuenta tiene un movimiento registrado de tipo {string}")
    public void cuentaTieneMovimientoTipo(String tipo) {
        Transaccion tx = Transaccion.builder().id(UUID.randomUUID()).build();
        HistorialTransaccionDto dto = HistorialTransaccionDto.builder().tipo(tipo).build();
        when(transaccionRepo.findByCuentaIdOrderByFechaDesc(any())).thenReturn(List.of(tx));
        when(historialMapper.toDto(tx)).thenReturn(dto);
    }
 
    @When("el cliente consulta el historial de la cuenta")
    public void consultaHistorial() {
        historialResult = historialUC.ejecutar(cuentaId);
    }
 
    @Then("la lista de movimientos tiene {int} elemento con tipo {string}")
    public void listaMovimientosTieneElemento(int cantidad, String tipo) {
        assertThat(historialResult).hasSize(cantidad);
        assertThat(historialResult.get(0).getTipo()).isEqualTo(tipo);
    }
 
    @Given("la cuenta no tiene ningun movimiento registrado")
    public void cuentaSinMovimientos() {
        when(transaccionRepo.findByCuentaIdOrderByFechaDesc(any())).thenReturn(Collections.emptyList());
    }
 
    @Then("la lista de movimientos esta vacia")
    public void listaMovimientosVacia() {
        assertThat(historialResult).isEmpty();
    }
}