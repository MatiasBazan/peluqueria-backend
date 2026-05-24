package com.mab.peluqueria.turno;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
        @NotNull EstadoTurno estado
) {}
