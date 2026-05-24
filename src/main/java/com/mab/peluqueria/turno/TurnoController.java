package com.mab.peluqueria.turno;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
@Tag(name = "Turnos", description = "Reserva, consulta y gestion de turnos")
public class TurnoController {

    private final TurnoService turnoService;
    private final DisponibilidadService disponibilidadService;

    @GetMapping("/disponibilidad")
    @Operation(summary = "Slots disponibles ese dia para un profesional+servicio (publico)")
    public DisponibilidadResponse disponibilidad(
            @RequestParam Long profesionalId,
            @RequestParam Long servicioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return disponibilidadService.calcularDisponibilidad(profesionalId, servicioId, fecha);
    }

    @PostMapping
    @Operation(summary = "Reserva un turno (publico)")
    @ResponseStatus(HttpStatus.CREATED)
    public TurnoResponse crear(@Valid @RequestBody CrearTurnoRequest req) {
        return turnoService.crear(req);
    }

    @GetMapping("/cliente")
    @Operation(summary = "Lista los turnos futuros del cliente identificado por telefono (publico)")
    public List<TurnoResponse> turnosDelCliente(@RequestParam String telefono) {
        return turnoService.listarFuturosPorTelefono(telefono);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela un turno usando el codigo enviado al cliente (publico)")
    public TurnoResponse cancelar(@PathVariable Long id, @RequestParam String codigo) {
        return turnoService.cancelarConCodigo(id, codigo);
    }

    @GetMapping
    @Operation(summary = "Lista turnos con filtros (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public List<TurnoResponse> listarAdmin(
            @RequestParam(required = false) Long profesionalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) EstadoTurno estado) {
        return turnoService.buscarParaAdmin(profesionalId, fecha, estado);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambia el estado de un turno (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public TurnoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest req) {
        return turnoService.cambiarEstado(id, req.estado());
    }
}
