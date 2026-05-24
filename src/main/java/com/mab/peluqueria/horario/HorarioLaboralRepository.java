package com.mab.peluqueria.horario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface HorarioLaboralRepository extends JpaRepository<HorarioLaboral, Long> {

    List<HorarioLaboral> findByProfesionalId(Long profesionalId);

    List<HorarioLaboral> findByProfesionalIdAndDiaSemana(Long profesionalId, DayOfWeek diaSemana);
}
