package com.mab.peluqueria.servicio;

import com.mab.peluqueria.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository repo;

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponse> listarTodos() {
        return repo.findAll().stream().map(ServicioResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponse> listarActivos() {
        return repo.findAllByActivoTrue().stream().map(ServicioResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponse obtener(Long id) {
        return ServicioResponse.from(buscar(id));
    }

    @Override
    public ServicioResponse crear(ServicioRequest r) {
        Servicio nuevo = Servicio.builder()
                .nombre(r.nombre())
                .descripcion(r.descripcion())
                .duracionMinutos(r.duracionMinutos())
                .precio(r.precio())
                .activo(r.activo())
                .build();
        return ServicioResponse.from(repo.save(nuevo));
    }

    @Override
    public ServicioResponse actualizar(Long id, ServicioRequest r) {
        Servicio s = buscar(id);
        s.setNombre(r.nombre());
        s.setDescripcion(r.descripcion());
        s.setDuracionMinutos(r.duracionMinutos());
        s.setPrecio(r.precio());
        s.setActivo(r.activo());
        return ServicioResponse.from(s);
    }

    @Override
    public void eliminar(Long id) {
        Servicio s = buscar(id);
        s.setActivo(false);
    }

    private Servicio buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Servicio " + id + " no encontrado"));
    }
}
