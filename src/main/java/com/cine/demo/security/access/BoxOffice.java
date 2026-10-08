package com.cine.demo.security.access;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Operacion de taquilla y tienda: gerencia y cajeros. */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('" + Authorities.MANAGEMENT + "', '" + Authorities.CASHIER + "')")
public @interface BoxOffice {
}
