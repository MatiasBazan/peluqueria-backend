package com.mab.peluqueria.servicio;

import java.util.List;

public interface ServicioService {
    List<ServicioResponse> listarTodos();
    List<ServicioResponse> listarActivos();
    ServicioResponse obtener(Long id);
    ServicioResponse crear(ServicioRequest request);
    ServicioResponse actualizar(Long id, ServicioRequest request);
    void eliminar(Long id);
}
