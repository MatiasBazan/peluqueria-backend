package com.mab.peluqueria.servicio;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ServicioRequest(
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 500) String descripcion,
        @NotNull @Min(5) @Max(480) Integer duracionMinutos,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precio,
        @NotNull Boolean activo
) {}
