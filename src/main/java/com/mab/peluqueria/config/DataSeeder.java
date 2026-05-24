package com.mab.peluqueria.config;

import com.mab.peluqueria.auth.Rol;
import com.mab.peluqueria.auth.Usuario;
import com.mab.peluqueria.auth.UsuarioRepository;
import com.mab.peluqueria.cliente.Cliente;
import com.mab.peluqueria.cliente.ClienteRepository;
import com.mab.peluqueria.horario.HorarioLaboral;
import com.mab.peluqueria.horario.HorarioLaboralRepository;
import com.mab.peluqueria.profesional.Profesional;
import com.mab.peluqueria.profesional.ProfesionalRepository;
import com.mab.peluqueria.servicio.Servicio;
import com.mab.peluqueria.servicio.ServicioRepository;
import com.mab.peluqueria.turno.EstadoTurno;
import com.mab.peluqueria.turno.Turno;
import com.mab.peluqueria.turno.TurnoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepo;
    private final ServicioRepository servicioRepo;
    private final ProfesionalRepository profesionalRepo;
    private final HorarioLaboralRepository horarioRepo;
    private final ClienteRepository clienteRepo;
    private final TurnoRepository turnoRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepo.count() > 0) {
            log.info("DataSeeder: la BD ya tiene datos, salteando seed.");
            return;
        }

        log.info("DataSeeder: cargando datos demo...");

        // Usuario admin
        Usuario admin = Usuario.builder()
                .email("admin@peluqueria.com")
                .password(passwordEncoder.encode("admin1234"))
                .nombre("Administrador")
                .rol(Rol.ADMIN)
                .build();
        usuarioRepo.save(admin);

        // Servicios
        Servicio corte = servicioRepo.save(Servicio.builder()
                .nombre("Corte de cabello")
                .descripcion("Corte clasico o moderno con lavado incluido.")
                .duracionMinutos(30)
                .precio(new BigDecimal("4500.00"))
                .activo(true)
                .build());
        Servicio color = servicioRepo.save(Servicio.builder()
                .nombre("Coloracion")
                .descripcion("Tintura completa con acondicionador.")
                .duracionMinutos(90)
                .precio(new BigDecimal("12000.00"))
                .activo(true)
                .build());
        Servicio barba = servicioRepo.save(Servicio.builder()
                .nombre("Arreglo de barba")
                .descripcion("Perfilado, recorte y toalla caliente.")
                .duracionMinutos(20)
                .precio(new BigDecimal("3000.00"))
                .activo(true)
                .build());
        Servicio peinado = servicioRepo.save(Servicio.builder()
                .nombre("Peinado para evento")
                .descripcion("Brushing o recogido para fiesta.")
                .duracionMinutos(60)
                .precio(new BigDecimal("8500.00"))
                .activo(true)
                .build());

        // Profesionales
        Profesional lucia = profesionalRepo.save(Profesional.builder()
                .nombre("Lucia")
                .apellido("Martinez")
                .telefono("+5491100000001")
                .foto("https://i.pravatar.cc/200?img=47")
                .activo(true)
                .servicios(Set.of(corte, color, peinado))
                .build());
        Profesional martin = profesionalRepo.save(Profesional.builder()
                .nombre("Martin")
                .apellido("Gomez")
                .telefono("+5491100000002")
                .foto("https://i.pravatar.cc/200?img=12")
                .activo(true)
                .servicios(Set.of(corte, barba))
                .build());
        Profesional sofia = profesionalRepo.save(Profesional.builder()
                .nombre("Sofia")
                .apellido("Lopez")
                .telefono("+5491100000003")
                .foto("https://i.pravatar.cc/200?img=32")
                .activo(true)
                .servicios(Set.of(corte, color, peinado, barba))
                .build());

        // Horarios laborales (martes a sabado, mañana y tarde)
        List<DayOfWeek> diasLaborales = List.of(
                DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        );

        for (DayOfWeek dia : diasLaborales) {
            // Lucia: 9 a 13 y 15 a 19
            crearHorario(lucia, dia, LocalTime.of(9, 0), LocalTime.of(13, 0));
            crearHorario(lucia, dia, LocalTime.of(15, 0), LocalTime.of(19, 0));
            // Martin: 10 a 18 corrido
            crearHorario(martin, dia, LocalTime.of(10, 0), LocalTime.of(18, 0));
            // Sofia: 11 a 20 corrido (no trabaja sabado)
            if (dia != DayOfWeek.SATURDAY) {
                crearHorario(sofia, dia, LocalTime.of(11, 0), LocalTime.of(20, 0));
            }
        }

        // Cliente y turnos de ejemplo
        Cliente clienteDemo = clienteRepo.save(Cliente.builder()
                .nombre("Juan Perez")
                .telefono("+5491155555555")
                .email("juan.perez@example.com")
                .build());

        LocalDate proximoLaboral = proximoDiaLaboral(LocalDate.now());
        crearTurnoDemo(clienteDemo, lucia, corte, proximoLaboral.atTime(10, 0));
        crearTurnoDemo(clienteDemo, martin, barba, proximoLaboral.atTime(11, 30));

        log.info("DataSeeder: listo. Credenciales admin -> admin@peluqueria.com / admin1234");
    }

    private void crearHorario(Profesional p, DayOfWeek dia, LocalTime ini, LocalTime fin) {
        horarioRepo.save(HorarioLaboral.builder()
                .profesional(p)
                .diaSemana(dia)
                .horaInicio(ini)
                .horaFin(fin)
                .build());
    }

    private void crearTurnoDemo(Cliente c, Profesional p, Servicio s, java.time.LocalDateTime cuando) {
        turnoRepo.save(Turno.builder()
                .cliente(c)
                .profesional(p)
                .servicio(s)
                .fechaHora(cuando)
                .estado(EstadoTurno.CONFIRMADO)
                .codigoCancelacion(UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase())
                .build());
    }

    private LocalDate proximoDiaLaboral(LocalDate hoy) {
        LocalDate fecha = hoy.plusDays(1);
        while (fecha.getDayOfWeek() == DayOfWeek.SUNDAY || fecha.getDayOfWeek() == DayOfWeek.MONDAY) {
            fecha = fecha.plusDays(1);
        }
        return fecha;
    }
}
