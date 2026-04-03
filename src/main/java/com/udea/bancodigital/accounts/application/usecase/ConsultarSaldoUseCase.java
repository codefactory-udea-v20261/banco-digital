package  com.udea.bancodigital.accounts.application.usecase;
import java.util.UUID;

import com.udea.bancodigital.accounts.application.dto.ConsultarSaldoResponseDto;
import com.udea.bancodigital.accounts.domain.exception.CuentaInactivaException;
import com.udea.bancodigital.accounts.domain.exception.CuentaNoEncontradaException;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;
import com.udea.bancodigital.accounts.domain.port.out.ClienteServicePort;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsultarSaldoUseCase {
    /*se trae el repositorio  de cuenta para buscar la id de la cuenta*/
    private final CuentaRepositoryPort cuentaRepository;

    /*se busca la cuenta en base a la id, en caso de no encontrarla
    arroja un RuntimeException*/
    public ConsultarSaldoResponseDto consultarSaldo(UUID cuentaId){
        Cuenta cuenta=cuentaRepository.findById(cuentaId).orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));
    
    /*verifica que la cuenta este activa, en caso de no estarlo
    arroja un RuntimeException*/
    if (!cuenta.getEstado().equals(EstadoCuenta.ACTIVA)) {
        throw new CuentaInactivaException(cuentaId);
    }
    return ConsultarSaldoResponseDto.builder()
            .saldo(cuenta.getSaldo())
            .build();    }

}