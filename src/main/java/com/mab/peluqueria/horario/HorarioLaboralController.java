package com.mab.peluqueria.horario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
@Tag(name = "Horarios laborales", description = "Horarios de trabajo de los profesionales")
public class HorarioLaboralController {

    private final HorarioLaboralService service;

    @GetMapping
    @Operation(summary = "Lista los horarios de un profesional (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public List<HorarioLaboralResponse> listar(@RequestParam Long profesionalId) {
        return service.listarPorProfesional(profesionalId);
    }

    @PostMapping
    @Operation(summary = "Crea un horario laboral (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioLaboralResponse crear(@Valid @RequestBody HorarioLaboralRequest req) {
        return service.crear(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un horario laboral (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public HorarioLaboralResponse actualizar(@PathVariable Long id, @Valid @RequestBody HorarioLaboralRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un horario laboral (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
