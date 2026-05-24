package com.mab.peluqueria.turno;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TurnoResponse(
        Long id,
        Long profesionalId,
        String profesionalNombre,
        Long servicioId,
        String servicioNombre,
        Integer duracionMinutos,
        String clienteNombre,
        String clienteTelefono,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaHora,
        EstadoTurno estado,
        String codigoCancelacion
) {
    public static TurnoResponse from(Turno t) {
        return new TurnoResponse(
                t.getId(),
                t.getProfesional().getId(),
                t.getProfesional().getNombre() + " " + t.getProfesional().getApellido(),
                t.getServicio().getId(),
                t.getServicio().getNombre(),
                t.getServicio().getDuracionMinutos(),
                t.getCliente().getNombre(),
                t.getCliente().getTelefono(),
                t.getFechaHora(),
                t.getEstado(),
                t.getCodigoCancelacion()
        );
    }

    public static TurnoResponse sinCodigo(Turno t) {
        return new TurnoResponse(
                t.getId(),
                t.getProfesional().getId(),
                t.getProfesional().getNombre() + " " + t.getProfesional().getApellido(),
                t.getServicio().getId(),
                t.getServicio().getNombre(),
                t.getServicio().getDuracionMinutos(),
                t.getCliente().getNombre(),
                t.getCliente().getTelefono(),
                t.getFechaHora(),
                t.getEstado(),
                null
        );
    }
}
