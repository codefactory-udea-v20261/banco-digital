package com.udea.bancodigital.transactions.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
public enum TipoTransaccion {
    RETIRO((short) 1, "Retiro de efectivo"),
    DEPOSITO((short) 2, "Depósito de efectivo"),
    TRANSFERENCIA_ENVIADA((short) 3, "Transferencia enviada"),
    TRANSFERENCIA_RECIBIDA((short) 4, "Transferencia recibida");

    private final short id;
    private final String descripcion;

    TipoTransaccion(short id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    /**
     * MÉTODO QUE FALTA: Busca el Enum correspondiente a un ID.
     * Si no se encuentra, puede devolver null o lanzar una excepción.
     */
    public static TipoTransaccion fromId(Short id) {
        if (id == null) return null;

        return Arrays.stream(TipoTransaccion.values())
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
        // O puedes lanzar: .orElseThrow(() -> new IllegalArgumentException("ID de transacción inválido: " + id));
    }


}
