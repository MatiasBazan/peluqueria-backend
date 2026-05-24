package com.mab.peluqueria.notificacion;

import com.mab.peluqueria.turno.Turno;

public interface NotificacionService {

    void enviarConfirmacionTurno(Turno turno);

    void enviarCancelacionTurno(Turno turno);
}
