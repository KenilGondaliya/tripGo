package com.example.tripGo.error;

public record ApiError(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {}
