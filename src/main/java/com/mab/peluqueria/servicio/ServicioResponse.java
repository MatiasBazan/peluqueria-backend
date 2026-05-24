package com.mab.peluqueria.servicio;

import java.math.BigDecimal;

public record ServicioResponse(
        Long id,
        String nombre,
        String descripcion,
        Integer duracionMinutos,
        BigDecimal precio,
        boolean activo
) {
    public static ServicioResponse from(Servicio s) {
        return new ServicioResponse(
                s.getId(),
                s.getNombre(),
                s.getDescripcion(),
                s.getDuracionMinutos(),
                s.getPrecio(),
                s.isActivo()
        );
    }
}
