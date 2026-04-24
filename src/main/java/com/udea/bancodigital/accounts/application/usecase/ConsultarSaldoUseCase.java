package  com.udea.bancodigital.accounts.application.usecase;

import java.util.UUID;

import com.udea.bancodigital.accounts.application.dto.ConsultarSaldoResponseDto;
import com.udea.bancodigital.accounts.domain.exception.CuentaInactivaException;
import com.udea.bancodigital.accounts.domain.exception.CuentaNoEncontradaException;
import com.udea.bancodigital.accounts.domain.exception.CuentaNoPerteneceAlClienteException;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;
import com.udea.bancodigital.accounts.domain.port.in.ConsultarSaldoPort;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ConsultarSaldoUseCase implements ConsultarSaldoPort {
    
    private final CuentaRepositoryPort cuentaRepository;

   
    @Override
    public ConsultarSaldoResponseDto consultarSaldo(UUID cuentaId, UUID clienteId){
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
            .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));
    
        if (!cuenta.getClienteId().equals(clienteId)) {
            log.warn("Intento de acceso no autorizado: Cliente {} intentó consultar saldo de cuenta {} que no le pertenece", 
                     clienteId, cuentaId);
            throw new CuentaNoPerteneceAlClienteException(cuentaId);
        }

        if (!cuenta.getEstado().equals(EstadoCuenta.ACTIVA)) {
            log.warn("Intento de consulta en cuenta inactiva: Cliente {} intentó consultar cuenta {} con estado {}", 
                     clienteId, cuentaId, cuenta.getEstado());
            throw new CuentaInactivaException(cuentaId);
        }

        log.info("Consulta de saldo exitosa: Cliente {} consultó saldo de cuenta {} - Saldo: {}", 
                 clienteId, cuentaId, cuenta.getSaldo());
        
        return ConsultarSaldoResponseDto.builder()
                .saldo(cuenta.getSaldo())
                .build();
    }
}
