package com.mab.peluqueria.profesional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ProfesionalRequest(
        @NotBlank @Size(max = 80) String nombre,
        @NotBlank @Size(max = 80) String apellido,
        @Size(max = 30) String telefono,
        @Size(max = 500) String foto,
        @NotNull Boolean activo,
        @NotNull Set<Long> servicioIds
) {}
