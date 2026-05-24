package com.mab.peluqueria.auth;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEnMs,
        String email,
        String nombre,
        Rol rol
) {
    public static LoginResponse bearer(String token, long expiraEnMs, Usuario u) {
        return new LoginResponse(token, "Bearer", expiraEnMs, u.getEmail(), u.getNombre(), u.getRol());
    }
}
