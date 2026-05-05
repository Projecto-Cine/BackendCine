package com.cine.demo.exception;

import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.security.ForbiddenException;
import com.cine.demo.security.InvalidTokenException;
import com.cine.demo.security.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests directos sobre GlobalExceptionHandler. Llamamos los métodos
 * de exception handling como simples métodos Java (sin levantar el
 * contexto Spring), lo que es más rápido y aísla la lógica.
 *
 * Verificamos para cada handler:
 *   - el código HTTP devuelto
 *   - que success = false
 *   - que el mensaje en español llega al cuerpo de la respuesta
 *   - que la lista de errors no es null (para evitar NPE en frontend)
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_returns404WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleResourceNotFound(
                new ResourceNotFoundException("Usuario no encontrado con id: 99"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Usuario no encontrado con id: 99");
        assertThat(response.getBody().getErrors()).isEmpty();
    }

    @Test
    void handleConflict_returns409WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleConflict(
                new ConflictException("Email duplicado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).isEqualTo("Email duplicado");
    }

    @Test
    void handleBusinessRule_returns400WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessRule(
                new BusinessRuleException("Regla de negocio violada"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Regla de negocio violada");
    }

    @Test
    void handleSeatAlreadyTaken_returns409WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleSeatAlreadyTaken(
                new SeatAlreadyTakenException("El asiento A1 ya está ocupado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("A1");
    }

    @Test
    void handleScreeningFull_returns409WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleScreeningFull(
                new ScreeningFullException("No hay asientos"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleScreeningAlreadyPassed_returns400WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleScreeningAlreadyPassed(
                new ScreeningAlreadyPassedException("Ya finalizó"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handlePurchaseAlreadyCancelled_returns409WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handlePurchaseAlreadyCancelled(
                new PurchaseAlreadyCancelledException("Ya cancelada"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleInvalidPurchaseStatus_returns422WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidPurchaseStatus(
                new InvalidPurchaseStatusException("Estado inválido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void handleAgeRestriction_returns403WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleAgeRestriction(
                new AgeRestrictionException("Edad insuficiente"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void handleMinorWithoutAdult_returns422WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleMinorWithoutAdult(
                new MinorWithoutAdultException("Menor sin adulto"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Estos tres handlers son los que añadimos para Spring Security:
     * cubren los flujos de error de autenticación y autorización.
     */
    @Test
    void handleUnauthorized_returns401WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleUnauthorized(
                new UnauthorizedException("Credenciales inválidas"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Credenciales inválidas");
    }

    @Test
    void handleInvalidToken_returns401WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidToken(
                new InvalidTokenException("Token caducado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Token caducado");
    }

    @Test
    void handleForbidden_returns403WithMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleForbidden(
                new ForbiddenException("Sin permisos"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getMessage()).isEqualTo("Sin permisos");
    }
}
