package com.cine.demo.exception;

import com.cine.demo.dto.response.ApiError;
import com.cine.demo.security.ForbiddenException;
import com.cine.demo.security.InvalidTokenException;
import com.cine.demo.security.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_returns404WithMessageAndTimestamp() {
        ResponseEntity<ApiError> response = handler.handleResourceNotFound(
                new ResourceNotFoundException("User not found with id: 99"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("User not found with id: 99");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleConflict_returns409WithMessage() {
        ResponseEntity<ApiError> response = handler.handleConflict(
                new ConflictException("Duplicate email"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).isEqualTo("Duplicate email");
    }

    @Test
    void handleBusinessRule_returns400WithMessage() {
        ResponseEntity<ApiError> response = handler.handleBusinessRule(
                new BusinessRuleException("Business rule violated"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Business rule violated");
    }

    @Test
    void handleSeatAlreadyTaken_returns409WithMessage() {
        ResponseEntity<ApiError> response = handler.handleSeatAlreadyTaken(
                new SeatAlreadyTakenException("Seat A1 is already occupied"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("A1");
    }

    @Test
    void handleScreeningFull_returns409WithMessage() {
        ResponseEntity<ApiError> response = handler.handleScreeningFull(
                new ScreeningFullException("No seats available"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).isEqualTo("No seats available");
    }

    @Test
    void handleScreeningAlreadyPassed_returns400WithMessage() {
        ResponseEntity<ApiError> response = handler.handleScreeningAlreadyPassed(
                new ScreeningAlreadyPassedException("Screening has ended"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Screening has ended");
    }

    @Test
    void handlePurchaseAlreadyCancelled_returns409WithMessage() {
        ResponseEntity<ApiError> response = handler.handlePurchaseAlreadyCancelled(
                new PurchaseAlreadyCancelledException("Already cancelled"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).isEqualTo("Already cancelled");
    }

    @Test
    void handleInvalidPurchaseStatus_returns422WithMessage() {
        ResponseEntity<ApiError> response = handler.handleInvalidPurchaseStatus(
                new InvalidPurchaseStatusException("Invalid status"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid status");
    }

    @Test
    void handleAgeRestriction_returns403WithMessage() {
        ResponseEntity<ApiError> response = handler.handleAgeRestriction(
                new AgeRestrictionException("Minimum age not met"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getMessage()).isEqualTo("Minimum age not met");
    }

    @Test
    void handleMinorWithoutAdult_returns422WithMessage() {
        ResponseEntity<ApiError> response = handler.handleMinorWithoutAdult(
                new MinorWithoutAdultException("Minor without adult"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().getMessage()).isEqualTo("Minor without adult");
    }

    @Test
    void handleUnauthorized_returns401WithMessage() {
        ResponseEntity<ApiError> response = handler.handleSecurityUnauthorized(
                new UnauthorizedException("Invalid credentials"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid credentials");
    }

    @Test
    void handleInvalidToken_returns401WithMessage() {
        ResponseEntity<ApiError> response = handler.handleInvalidToken(
                new InvalidTokenException("Token expired"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Token expired");
    }

    @Test
    void handleForbidden_returns403WithMessage() {
        ResponseEntity<ApiError> response = handler.handleForbidden(
                new ForbiddenException("Access denied"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
    }
}
