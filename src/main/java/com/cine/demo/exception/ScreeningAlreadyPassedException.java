package com.cine.demo.exception;

public class ScreeningAlreadyPassedException extends RuntimeException {
    public ScreeningAlreadyPassedException(String message) {
        super(message);
    }
}
