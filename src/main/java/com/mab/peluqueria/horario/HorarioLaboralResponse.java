package com.mab.peluqueria.horario;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioLaboralResponse(
        Long id,
        Long profesionalId,
        DayOfWeek diaSemana,
        @JsonFormat(pattern = "HH:mm") LocalTime horaInicio,
        @JsonFormat(pattern = "HH:mm") LocalTime horaFin
) {
    public static HorarioLaboralResponse from(HorarioLaboral h) {
        return new HorarioLaboralResponse(
                h.getId(),
                h.getProfesional().getId(),
                h.getDiaSemana(),
                h.getHoraInicio(),
                h.getHoraFin()
        );
    }
}
