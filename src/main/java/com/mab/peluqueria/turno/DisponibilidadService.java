package com.mab.peluqueria.turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DisponibilidadService {

    /**
     * Calcula los horarios disponibles ese dia para que un cliente pueda reservar
     * el servicio indicado con el profesional indicado.
     */
    DisponibilidadResponse calcularDisponibilidad(Long profesionalId, Long servicioId, LocalDate fecha);

    /**
     * Devuelve true si el slot exacto (fecha + hora) sigue disponible para reservar.
     */
    boolean estaDisponible(Long profesionalId, Long servicioId, LocalDate fecha, LocalTime hora);

    /**
     * Variante que devuelve solo los LocalTime (usada internamente y por los tests).
     */
    List<LocalTime> calcularSlots(Long profesionalId, Long servicioId, LocalDate fecha);
}
