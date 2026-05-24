package com.mab.peluqueria.profesional;

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
@RequestMapping("/api/profesionales")
@RequiredArgsConstructor
@Tag(name = "Profesionales", description = "Profesionales de la peluqueria")
public class ProfesionalController {

    private final ProfesionalService service;

    @GetMapping("/publicos")
    @Operation(summary = "Lista los profesionales activos con sus servicios (publico)")
    public List<ProfesionalResponse> listarActivos() {
        return service.listarActivos();
    }

    @GetMapping
    @Operation(summary = "Lista todos los profesionales (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public List<ProfesionalResponse> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un profesional por id (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ProfesionalResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea un profesional (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(HttpStatus.CREATED)
    public ProfesionalResponse crear(@Valid @RequestBody ProfesionalRequest req) {
        return service.crear(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un profesional (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ProfesionalResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProfesionalRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Da de baja un profesional (soft delete) (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
