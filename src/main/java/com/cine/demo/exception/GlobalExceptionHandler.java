package com.cine.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) { return null; }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<?> handleBusinessRule(BusinessRuleException ex) { return null; }

    @ExceptionHandler(SeatAlreadyTakenException.class)
    public ResponseEntity<?> handleSeatAlreadyTaken(SeatAlreadyTakenException ex) { return null; }

    @ExceptionHandler(ScreeningFullException.class)
    public ResponseEntity<?> handleScreeningFull(ScreeningFullException ex) { return null; }

    @ExceptionHandler(ScreeningAlreadyPassedException.class)
    public ResponseEntity<?> handleScreeningAlreadyPassed(ScreeningAlreadyPassedException ex) { return null; }
}
