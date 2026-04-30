package com.cine.demo.exception;

public class PurchaseAlreadyCancelledException extends RuntimeException {
    public PurchaseAlreadyCancelledException(String message) {
        super(message);
    }
}
