package com.mab.peluqueria.notificacion;

import com.mab.peluqueria.turno.Turno;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@ConditionalOnProperty(name = "notificacion.provider", havingValue = "mock", matchIfMissing = true)
public class WhatsappNotificacionMockService implements NotificacionService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void enviarConfirmacionTurno(Turno turno) {
        String mensaje = String.format(
                "[MOCK WhatsApp -> %s] Hola %s! Te confirmamos tu turno de %s con %s %s el %s. " +
                        "Codigo de cancelacion: %s",
                turno.getCliente().getTelefono(),
                turno.getCliente().getNombre(),
                turno.getServicio().getNombre(),
                turno.getProfesional().getNombre(),
                turno.getProfesional().getApellido(),
                turno.getFechaHora().format(FMT),
                turno.getCodigoCancelacion()
        );
        log.info(mensaje);
    }

    @Override
    public void enviarCancelacionTurno(Turno turno) {
        String mensaje = String.format(
                "[MOCK WhatsApp -> %s] Hola %s, tu turno del %s fue cancelado.",
                turno.getCliente().getTelefono(),
                turno.getCliente().getNombre(),
                turno.getFechaHora().format(FMT)
        );
        log.info(mensaje);
    }
}
