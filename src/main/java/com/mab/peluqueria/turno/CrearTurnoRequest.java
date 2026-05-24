package com.mab.peluqueria.turno;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CrearTurnoRequest(
        @NotNull Long profesionalId,
        @NotNull Long servicioId,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaHora,
        @NotBlank @Size(max = 120) String nombreCliente,
        @NotBlank @Size(max = 30) String telefonoCliente,
        @Email @Size(max = 120) String emailCliente
) {}
