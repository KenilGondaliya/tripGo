package com.example.tripGo.error;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}