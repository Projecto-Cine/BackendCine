package com.cine.demo.exception;

import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.security.ForbiddenException;
import com.cine.demo.security.InvalidTokenException;
import com.cine.demo.security.UnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(BusinessRuleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(SeatAlreadyTakenException.class)
    public ResponseEntity<ApiResponse<Void>> handleSeatAlreadyTaken(SeatAlreadyTakenException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(ScreeningFullException.class)
    public ResponseEntity<ApiResponse<Void>> handleScreeningFull(ScreeningFullException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(ScreeningAlreadyPassedException.class)
    public ResponseEntity<ApiResponse<Void>> handleScreeningAlreadyPassed(ScreeningAlreadyPassedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(PurchaseAlreadyCancelledException.class)
    public ResponseEntity<ApiResponse<Void>> handlePurchaseAlreadyCancelled(PurchaseAlreadyCancelledException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(InvalidPurchaseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPurchaseStatus(InvalidPurchaseStatusException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(AgeRestrictionException.class)
    public ResponseEntity<ApiResponse<Void>> handleAgeRestriction(AgeRestrictionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(MinorWithoutAdultException.class)
    public ResponseEntity<ApiResponse<Void>> handleMinorWithoutAdult(MinorWithoutAdultException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message("Validation failed")
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> {
                            String path = v.getPropertyPath().toString();
                            return path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                        },
                        v -> v.getMessage(),
                        (existing, replacement) -> existing
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message("Validation failed")
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String detail = "Must be of type "
                + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message("Invalid parameter type")
                        .errors(Map.of(ex.getName(), detail))
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message("Failed to read request body: " + ex.getMessage())
                        .errors(Map.of())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message("Internal server error: " + ex.getClass().getSimpleName() + " - " + ex.getMessage())
                        .errors(Map.of())
                        .build());
    }
}
