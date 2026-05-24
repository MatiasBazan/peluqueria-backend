package com.mab.peluqueria.profesional;

import com.mab.peluqueria.common.NotFoundException;
import com.mab.peluqueria.servicio.Servicio;
import com.mab.peluqueria.servicio.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfesionalServiceImpl implements ProfesionalService {

    private final ProfesionalRepository repo;
    private final ServicioRepository servicioRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ProfesionalResponse> listarTodos() {
        return repo.findAll().stream().map(ProfesionalResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesionalResponse> listarActivos() {
        return repo.findAllActivosConServicios().stream().map(ProfesionalResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProfesionalResponse obtener(Long id) {
        return ProfesionalResponse.from(buscar(id));
    }

    @Override
    public ProfesionalResponse crear(ProfesionalRequest r) {
        Profesional nuevo = Profesional.builder()
                .nombre(r.nombre())
                .apellido(r.apellido())
                .telefono(r.telefono())
                .foto(r.foto())
                .activo(r.activo())
                .servicios(resolverServicios(r.servicioIds()))
                .build();
        return ProfesionalResponse.from(repo.save(nuevo));
    }

    @Override
    public ProfesionalResponse actualizar(Long id, ProfesionalRequest r) {
        Profesional p = buscar(id);
        p.setNombre(r.nombre());
        p.setApellido(r.apellido());
        p.setTelefono(r.telefono());
        p.setFoto(r.foto());
        p.setActivo(r.activo());
        p.setServicios(resolverServicios(r.servicioIds()));
        return ProfesionalResponse.from(p);
    }

    @Override
    public void eliminar(Long id) {
        Profesional p = buscar(id);
        p.setActivo(false);
    }

    private Profesional buscar(Long id) {
        return repo.findByIdConServicios(id)
                .orElseThrow(() -> new NotFoundException("Profesional " + id + " no encontrado"));
    }

    private Set<Servicio> resolverServicios(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        List<Servicio> encontrados = servicioRepo.findAllById(ids);
        if (encontrados.size() != ids.size()) {
            throw new NotFoundException("Uno o mas servicios no existen");
        }
        return new HashSet<>(encontrados);
    }
}
