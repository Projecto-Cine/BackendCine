package com.cine.demo.exception;

import com.cine.demo.security.ForbiddenException;
import com.cine.demo.security.InvalidTokenException;
import com.cine.demo.security.UnauthorizedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para los constructores de las excepciones de negocio.
 * Aunque parezca trivial, JaCoCo cuenta el constructor como código a cubrir;
 * si no instanciamos cada excepción nunca aparecen como cubiertas.
 *
 * Cada test verifica que el mensaje pasado al constructor llega a getMessage().
 */
class ExceptionsTest {

    @Test
    void businessRuleException_propagatesMessage() {
        BusinessRuleException ex = new BusinessRuleException("Regla rota");
        assertThat(ex.getMessage()).isEqualTo("Regla rota");
    }

    @Test
    void forbiddenException_propagatesMessage() {
        ForbiddenException ex = new ForbiddenException("Acceso prohibido");
        assertThat(ex.getMessage()).isEqualTo("Acceso prohibido");
    }

    @Test
    void unauthorizedException_propagatesMessage() {
        UnauthorizedException ex = new UnauthorizedException("No autorizado");
        assertThat(ex.getMessage()).isEqualTo("No autorizado");
    }

    @Test
    void invalidTokenException_propagatesMessage() {
        InvalidTokenException ex = new InvalidTokenException("Token caducado");
        assertThat(ex.getMessage()).isEqualTo("Token caducado");
    }

    @Test
    void resourceNotFoundException_propagatesMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("No existe");
        assertThat(ex.getMessage()).isEqualTo("No existe");
    }

    @Test
    void conflictException_propagatesMessage() {
        ConflictException ex = new ConflictException("Conflicto");
        assertThat(ex.getMessage()).isEqualTo("Conflicto");
    }

    @Test
    void seatAlreadyTakenException_propagatesMessage() {
        SeatAlreadyTakenException ex = new SeatAlreadyTakenException("Ocupado");
        assertThat(ex.getMessage()).isEqualTo("Ocupado");
    }

    @Test
    void screeningFullException_propagatesMessage() {
        ScreeningFullException ex = new ScreeningFullException("Llena");
        assertThat(ex.getMessage()).isEqualTo("Llena");
    }

    @Test
    void screeningAlreadyPassedException_propagatesMessage() {
        ScreeningAlreadyPassedException ex = new ScreeningAlreadyPassedException("Ya pasó");
        assertThat(ex.getMessage()).isEqualTo("Ya pasó");
    }

    @Test
    void purchaseAlreadyCancelledException_propagatesMessage() {
        PurchaseAlreadyCancelledException ex = new PurchaseAlreadyCancelledException("Ya cancelada");
        assertThat(ex.getMessage()).isEqualTo("Ya cancelada");
    }

    @Test
    void invalidPurchaseStatusException_propagatesMessage() {
        InvalidPurchaseStatusException ex = new InvalidPurchaseStatusException("Estado inválido");
        assertThat(ex.getMessage()).isEqualTo("Estado inválido");
    }

    @Test
    void ageRestrictionException_propagatesMessage() {
        AgeRestrictionException ex = new AgeRestrictionException("Edad insuficiente");
        assertThat(ex.getMessage()).isEqualTo("Edad insuficiente");
    }

    @Test
    void minorWithoutAdultException_propagatesMessage() {
        MinorWithoutAdultException ex = new MinorWithoutAdultException("Menor sin adulto");
        assertThat(ex.getMessage()).isEqualTo("Menor sin adulto");
    }
}
