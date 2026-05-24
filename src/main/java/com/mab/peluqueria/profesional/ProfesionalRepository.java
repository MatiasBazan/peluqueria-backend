package com.mab.peluqueria.profesional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {

    @Query("select distinct p from Profesional p left join fetch p.servicios where p.activo = true")
    List<Profesional> findAllActivosConServicios();

    @Query("select p from Profesional p left join fetch p.servicios where p.id = :id")
    Optional<Profesional> findByIdConServicios(Long id);
}
