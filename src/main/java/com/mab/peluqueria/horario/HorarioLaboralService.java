package com.mab.peluqueria.horario;

import java.util.List;

public interface HorarioLaboralService {
    List<HorarioLaboralResponse> listarPorProfesional(Long profesionalId);
    HorarioLaboralResponse crear(HorarioLaboralRequest request);
    HorarioLaboralResponse actualizar(Long id, HorarioLaboralRequest request);
    void eliminar(Long id);
}
