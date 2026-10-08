package com.cine.demo.security.access;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cualquier empleado, incluidos limpieza y mantenimiento. Pensado para lo que
 * toda la plantilla necesita consultar, como su propio cuadrante de turnos.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('" + Authorities.MANAGEMENT + "', '" + Authorities.CASHIER
        + "', '" + Authorities.CLEANING + "', '" + Authorities.MAINTENANCE + "')")
public @interface AnyEmployee {
}
