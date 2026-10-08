package com.cine.demo.security.access;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Incidencias: las registra y resuelve mantenimiento, las supervisa gerencia. */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('" + Authorities.MANAGEMENT + "', '" + Authorities.MAINTENANCE + "')")
public @interface MaintenanceOrManagement {
}
