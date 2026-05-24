package com.mab.peluqueria.profesional;

import java.util.List;

public interface ProfesionalService {
    List<ProfesionalResponse> listarTodos();
    List<ProfesionalResponse> listarActivos();
    ProfesionalResponse obtener(Long id);
    ProfesionalResponse crear(ProfesionalRequest request);
    ProfesionalResponse actualizar(Long id, ProfesionalRequest request);
    void eliminar(Long id);
}
