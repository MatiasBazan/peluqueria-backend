package com.mab.peluqueria.horario;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioLaboralRequest(
        @NotNull Long profesionalId,
        @NotNull DayOfWeek diaSemana,
        @NotNull @JsonFormat(pattern = "HH:mm") LocalTime horaInicio,
        @NotNull @JsonFormat(pattern = "HH:mm") LocalTime horaFin
) {}
