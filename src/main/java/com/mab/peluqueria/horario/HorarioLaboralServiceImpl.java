package com.mab.peluqueria.horario;

import com.mab.peluqueria.common.BusinessException;
import com.mab.peluqueria.common.NotFoundException;
import com.mab.peluqueria.profesional.Profesional;
import com.mab.peluqueria.profesional.ProfesionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HorarioLaboralServiceImpl implements HorarioLaboralService {

    private final HorarioLaboralRepository repo;
    private final ProfesionalRepository profesionalRepo;

    @Override
    @Transactional(readOnly = true)
    public List<HorarioLaboralResponse> listarPorProfesional(Long profesionalId) {
        return repo.findByProfesionalId(profesionalId).stream()
                .map(HorarioLaboralResponse::from)
                .toList();
    }

    @Override
    public HorarioLaboralResponse crear(HorarioLaboralRequest r) {
        validarHoras(r);
        Profesional p = profesionalRepo.findById(r.profesionalId())
                .orElseThrow(() -> new NotFoundException("Profesional " + r.profesionalId() + " no encontrado"));
        HorarioLaboral nuevo = HorarioLaboral.builder()
                .profesional(p)
                .diaSemana(r.diaSemana())
                .horaInicio(r.horaInicio())
                .horaFin(r.horaFin())
                .build();
        return HorarioLaboralResponse.from(repo.save(nuevo));
    }

    @Override
    public HorarioLaboralResponse actualizar(Long id, HorarioLaboralRequest r) {
        validarHoras(r);
        HorarioLaboral h = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Horario " + id + " no encontrado"));
        if (!h.getProfesional().getId().equals(r.profesionalId())) {
            Profesional p = profesionalRepo.findById(r.profesionalId())
                    .orElseThrow(() -> new NotFoundException("Profesional " + r.profesionalId() + " no encontrado"));
            h.setProfesional(p);
        }
        h.setDiaSemana(r.diaSemana());
        h.setHoraInicio(r.horaInicio());
        h.setHoraFin(r.horaFin());
        return HorarioLaboralResponse.from(h);
    }

    @Override
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Horario " + id + " no encontrado");
        }
        repo.deleteById(id);
    }

    private void validarHoras(HorarioLaboralRequest r) {
        if (!r.horaInicio().isBefore(r.horaFin())) {
            throw new BusinessException("La hora de inicio debe ser anterior a la hora de fin");
        }
    }
}
