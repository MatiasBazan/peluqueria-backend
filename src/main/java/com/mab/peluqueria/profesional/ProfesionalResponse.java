package com.mab.peluqueria.profesional;

import com.mab.peluqueria.servicio.ServicioResponse;

import java.util.List;

public record ProfesionalResponse(
        Long id,
        String nombre,
        String apellido,
        String telefono,
        String foto,
        boolean activo,
        List<ServicioResponse> servicios
) {
    public static ProfesionalResponse from(Profesional p) {
        List<ServicioResponse> servs = p.getServicios().stream()
                .map(ServicioResponse::from)
                .sorted((a, b) -> a.nombre().compareToIgnoreCase(b.nombre()))
                .toList();
        return new ProfesionalResponse(
                p.getId(),
                p.getNombre(),
                p.getApellido(),
                p.getTelefono(),
                p.getFoto(),
                p.isActivo(),
                servs
        );
    }
}
