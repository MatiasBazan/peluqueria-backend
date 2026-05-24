package com.mab.peluqueria.turno;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record DisponibilidadResponse(
        Long profesionalId,
        Long servicioId,
        LocalDate fecha,
        Integer duracionMinutos,
        @JsonFormat(pattern = "HH:mm") List<LocalTime> slots
) {}
