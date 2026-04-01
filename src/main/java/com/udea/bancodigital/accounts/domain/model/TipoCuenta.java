package com.udea.bancodigital.accounts.domain.model;

import java.util.Arrays;

public enum TipoCuenta {
    AHORRO((short) 1, "AHORRO"),
    CORRIENTE((short) 2, "CORRIENTE");

    private final short id;
    private final String nombre;

    TipoCuenta(short id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public short getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static TipoCuenta fromId(Short id) {
        if (id == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(tipoCuenta -> tipoCuenta.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe un tipo de cuenta para el ID: " + id));
    }

    public static TipoCuenta fromNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        String normalizado = nombre.trim().toUpperCase();
        if ("AHORROS".equals(normalizado)) {
            normalizado = "AHORRO";
        }
        String valorBuscado = normalizado;
        return Arrays.stream(values())
                .filter(tipoCuenta -> tipoCuenta.nombre.equals(valorBuscado))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe un tipo de cuenta para el valor: " + nombre));
    }
}
