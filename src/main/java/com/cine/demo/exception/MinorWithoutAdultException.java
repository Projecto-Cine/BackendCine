package com.cine.demo.exception;

public class MinorWithoutAdultException extends RuntimeException {
    public MinorWithoutAdultException(String message) {
        super(message);
    }
}
