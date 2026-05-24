package com.mab.peluqueria.turno;

import com.mab.peluqueria.common.BusinessException;
import com.mab.peluqueria.common.NotFoundException;
import com.mab.peluqueria.horario.HorarioLaboral;
import com.mab.peluqueria.horario.HorarioLaboralRepository;
import com.mab.peluqueria.profesional.Profesional;
import com.mab.peluqueria.profesional.ProfesionalRepository;
import com.mab.peluqueria.servicio.Servicio;
import com.mab.peluqueria.servicio.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisponibilidadServiceImpl implements DisponibilidadService {

    private final ProfesionalRepository profesionalRepo;
    private final ServicioRepository servicioRepo;
    private final HorarioLaboralRepository horarioRepo;
    private final TurnoRepository turnoRepo;
    private final Clock clock;

    @Override
    public DisponibilidadResponse calcularDisponibilidad(Long profesionalId, Long servicioId, LocalDate fecha) {
        Servicio servicio = cargarServicioValidado(profesionalId, servicioId);
        List<LocalTime> slots = calcularSlotsInterno(profesionalId, servicio, fecha);
        return new DisponibilidadResponse(profesionalId, servicioId, fecha, servicio.getDuracionMinutos(), slots);
    }

    @Override
    public List<LocalTime> calcularSlots(Long profesionalId, Long servicioId, LocalDate fecha) {
        Servicio servicio = cargarServicioValidado(profesionalId, servicioId);
        return calcularSlotsInterno(profesionalId, servicio, fecha);
    }

    @Override
    public boolean estaDisponible(Long profesionalId, Long servicioId, LocalDate fecha, LocalTime hora) {
        return calcularSlots(profesionalId, servicioId, fecha).contains(hora);
    }

    private Servicio cargarServicioValidado(Long profesionalId, Long servicioId) {
        Profesional profesional = profesionalRepo.findByIdConServicios(profesionalId)
                .orElseThrow(() -> new NotFoundException("Profesional " + profesionalId + " no encontrado"));
        if (!profesional.isActivo()) {
            throw new BusinessException("El profesional no esta activo");
        }
        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new NotFoundException("Servicio " + servicioId + " no encontrado"));
        if (!servicio.isActivo()) {
            throw new BusinessException("El servicio no esta activo");
        }
        boolean ofreceServicio = profesional.getServicios().stream()
                .anyMatch(s -> s.getId().equals(servicioId));
        if (!ofreceServicio) {
            throw new BusinessException("El profesional no ofrece ese servicio");
        }
        return servicio;
    }

    private List<LocalTime> calcularSlotsInterno(Long profesionalId, Servicio servicio, LocalDate fecha) {
        int duracion = servicio.getDuracionMinutos();
        List<HorarioLaboral> horarios = horarioRepo.findByProfesionalIdAndDiaSemana(profesionalId, fecha.getDayOfWeek());
        if (horarios.isEmpty()) {
            return List.of();
        }

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();
        List<Turno> ocupados = turnoRepo.findOcupadosDelDia(profesionalId, inicioDia, finDia);

        LocalDateTime ahora = LocalDateTime.now(clock);
        boolean esHoy = fecha.equals(ahora.toLocalDate());

        List<LocalTime> slots = new ArrayList<>();
        for (HorarioLaboral bloque : horarios) {
            LocalTime cursor = bloque.getHoraInicio();
            while (!cursor.plusMinutes(duracion).isAfter(bloque.getHoraFin())) {
                LocalTime slot = cursor;
                LocalDateTime slotInicio = LocalDateTime.of(fecha, slot);
                LocalDateTime slotFin = slotInicio.plusMinutes(duracion);

                boolean enElPasado = esHoy && !slotInicio.isAfter(ahora);
                boolean chocaConOcupado = ocupados.stream().anyMatch(t -> seSolapan(slotInicio, slotFin, t));

                if (!enElPasado && !chocaConOcupado) {
                    slots.add(slot);
                }
                cursor = cursor.plusMinutes(duracion);
            }
        }

        slots.sort(Comparator.naturalOrder());
        return slots;
    }

    private boolean seSolapan(LocalDateTime aInicio, LocalDateTime aFin, Turno turno) {
        LocalDateTime bInicio = turno.getFechaHora();
        LocalDateTime bFin = bInicio.plusMinutes(turno.getServicio().getDuracionMinutos());
        return aInicio.isBefore(bFin) && bInicio.isBefore(aFin);
    }
}
