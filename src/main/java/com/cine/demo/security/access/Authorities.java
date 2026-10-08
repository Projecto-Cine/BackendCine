package com.cine.demo.security.access;

/**
 * Cadenas de authority que viajan en el claim "role" del JWT.
 *
 * Para empleados se usa el displayName de EmployeeRole (GERENCIA, CAJERO,
 * LIMPIEZA, MANTENIMIENTO) y no el name() del enum. No es casualidad: ese
 * displayName ya es el contrato externo del enum, porque va anotado con
 * @JsonValue y por tanto es lo que viaja en todos los DTO y lo que el frontend
 * usa para su matriz de permisos. Tener authorities en MANAGEMENT mientras la
 * API responde GERENCIA seria peor que mantener un unico vocabulario.
 *
 * Para usuarios (clientes y cuentas de sistema) se usa el name() de Role.
 */
public final class Authorities {

    private Authorities() {}

    public static final String MANAGEMENT = "GERENCIA";
    public static final String CASHIER = "CAJERO";
    public static final String CLEANING = "LIMPIEZA";
    public static final String MAINTENANCE = "MANTENIMIENTO";

    public static final String CLIENT = "CLIENT";
}
