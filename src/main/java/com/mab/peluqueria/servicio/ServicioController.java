package com.mab.peluqueria.servicio;

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
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Catalogo de servicios")
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping("/publicos")
    @Operation(summary = "Lista los servicios activos (publico)")
    public List<ServicioResponse> listarActivos() {
        return servicioService.listarActivos();
    }

    @GetMapping
    @Operation(summary = "Lista todos los servicios (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public List<ServicioResponse> listarTodos() {
        return servicioService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un servicio por id (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ServicioResponse obtener(@PathVariable Long id) {
        return servicioService.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea un servicio (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(HttpStatus.CREATED)
    public ServicioResponse crear(@Valid @RequestBody ServicioRequest req) {
        return servicioService.crear(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un servicio (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ServicioResponse actualizar(@PathVariable Long id, @Valid @RequestBody ServicioRequest req) {
        return servicioService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Da de baja un servicio (soft delete) (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
