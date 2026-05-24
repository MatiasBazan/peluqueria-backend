package com.mab.peluqueria.turno;

import com.mab.peluqueria.cliente.Cliente;
import com.mab.peluqueria.cliente.ClienteRepository;
import com.mab.peluqueria.common.BusinessException;
import com.mab.peluqueria.common.ConflictException;
import com.mab.peluqueria.common.NotFoundException;
import com.mab.peluqueria.notificacion.NotificacionService;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepo;
    private final ClienteRepository clienteRepo;
    private final ProfesionalRepository profesionalRepo;
    private final ServicioRepository servicioRepo;
    private final DisponibilidadService disponibilidadService;
    private final NotificacionService notificacionService;
    private final Clock clock;

    @Override
    public TurnoResponse crear(CrearTurnoRequest req) {
        if (req.fechaHora().isBefore(LocalDateTime.now(clock))) {
            throw new BusinessException("No se puede reservar un turno en el pasado");
        }

        Profesional profesional = profesionalRepo.findByIdConServicios(req.profesionalId())
                .orElseThrow(() -> new NotFoundException("Profesional " + req.profesionalId() + " no encontrado"));
        Servicio servicio = servicioRepo.findById(req.servicioId())
                .orElseThrow(() -> new NotFoundException("Servicio " + req.servicioId() + " no encontrado"));

        boolean disponible = disponibilidadService.estaDisponible(
                req.profesionalId(), req.servicioId(),
                req.fechaHora().toLocalDate(), req.fechaHora().toLocalTime()
        );
        if (!disponible) {
            throw new ConflictException("El horario seleccionado ya no esta disponible");
        }

        Cliente cliente = clienteRepo.findByTelefono(req.telefonoCliente())
                .map(c -> {
                    c.setNombre(req.nombreCliente());
                    if (req.emailCliente() != null && !req.emailCliente().isBlank()) {
                        c.setEmail(req.emailCliente());
                    }
                    return c;
                })
                .orElseGet(() -> clienteRepo.save(Cliente.builder()
                        .nombre(req.nombreCliente())
                        .telefono(req.telefonoCliente())
                        .email(req.emailCliente())
                        .build()));

        Turno nuevo = Turno.builder()
                .cliente(cliente)
                .profesional(profesional)
                .servicio(servicio)
                .fechaHora(req.fechaHora())
                .estado(EstadoTurno.PENDIENTE)
                .codigoCancelacion(generarCodigo())
                .build();
        Turno guardado = turnoRepo.save(nuevo);

        notificacionService.enviarConfirmacionTurno(guardado);
        return TurnoResponse.from(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponse> listarFuturosPorTelefono(String telefono) {
        return turnoRepo.findFuturosPorTelefono(telefono, LocalDateTime.now(clock)).stream()
                .map(TurnoResponse::sinCodigo)
                .toList();
    }

    @Override
    public TurnoResponse cancelarConCodigo(Long turnoId, String codigo) {
        Turno t = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new NotFoundException("Turno " + turnoId + " no encontrado"));
        if (!t.getCodigoCancelacion().equals(codigo)) {
            throw new BusinessException("Codigo de cancelacion invalido");
        }
        if (t.getEstado() == EstadoTurno.CANCELADO) {
            throw new BusinessException("El turno ya estaba cancelado");
        }
        if (t.getEstado() == EstadoTurno.COMPLETADO) {
            throw new BusinessException("No se puede cancelar un turno completado");
        }
        t.setEstado(EstadoTurno.CANCELADO);
        notificacionService.enviarCancelacionTurno(t);
        return TurnoResponse.sinCodigo(t);
    }

    @Override
    public TurnoResponse cambiarEstado(Long turnoId, EstadoTurno nuevoEstado) {
        Turno t = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new NotFoundException("Turno " + turnoId + " no encontrado"));
        t.setEstado(nuevoEstado);
        return TurnoResponse.from(t);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponse> buscarParaAdmin(Long profesionalId, LocalDate fecha, EstadoTurno estado) {
        LocalDateTime desde = fecha != null ? fecha.atStartOfDay() : null;
        LocalDateTime hasta = fecha != null ? fecha.plusDays(1).atStartOfDay() : null;
        return turnoRepo.buscarConFiltros(profesionalId, estado, desde, hasta).stream()
                .map(TurnoResponse::from)
                .toList();
    }

    private String generarCodigo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
