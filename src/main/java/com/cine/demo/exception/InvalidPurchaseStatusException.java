package com.cine.demo.exception;

public class InvalidPurchaseStatusException extends RuntimeException {
    public InvalidPurchaseStatusException(String message) {
        super(message);
    }
}
