package com.mab.peluqueria.turno;

import java.time.LocalDate;
import java.util.List;

public interface TurnoService {

    TurnoResponse crear(CrearTurnoRequest request);

    List<TurnoResponse> listarFuturosPorTelefono(String telefono);

    TurnoResponse cancelarConCodigo(Long turnoId, String codigo);

    TurnoResponse cambiarEstado(Long turnoId, EstadoTurno nuevoEstado);

    List<TurnoResponse> buscarParaAdmin(Long profesionalId, LocalDate fecha, EstadoTurno estado);
}
