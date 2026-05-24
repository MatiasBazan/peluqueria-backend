package com.mab.peluqueria.common;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> errores
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, null);
    }

    public static ErrorResponse of(int status, String error, String message, String path, List<FieldError> errores) {
        return new ErrorResponse(Instant.now(), status, error, message, path, errores);
    }

    public record FieldError(String campo, String mensaje) {}
}
