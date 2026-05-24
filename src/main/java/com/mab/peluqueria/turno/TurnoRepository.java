package com.mab.peluqueria.turno;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("""
            select t from Turno t
            where t.profesional.id = :profesionalId
              and t.fechaHora between :inicio and :fin
              and t.estado <> com.mab.peluqueria.turno.EstadoTurno.CANCELADO
            """)
    List<Turno> findOcupadosDelDia(@Param("profesionalId") Long profesionalId,
                                   @Param("inicio") LocalDateTime inicio,
                                   @Param("fin") LocalDateTime fin);

    @Query("""
            select t from Turno t
            join fetch t.cliente
            join fetch t.profesional
            join fetch t.servicio
            where t.cliente.telefono = :telefono
              and t.fechaHora >= :desde
              and t.estado <> com.mab.peluqueria.turno.EstadoTurno.CANCELADO
            order by t.fechaHora asc
            """)
    List<Turno> findFuturosPorTelefono(@Param("telefono") String telefono,
                                       @Param("desde") LocalDateTime desde);

    @Query("""
            select t from Turno t
            join fetch t.cliente
            join fetch t.profesional
            join fetch t.servicio
            where (:profesionalId is null or t.profesional.id = :profesionalId)
              and (:estado is null or t.estado = :estado)
              and (:desde is null or t.fechaHora >= :desde)
              and (:hasta is null or t.fechaHora < :hasta)
            order by t.fechaHora asc
            """)
    List<Turno> buscarConFiltros(@Param("profesionalId") Long profesionalId,
                                 @Param("estado") EstadoTurno estado,
                                 @Param("desde") LocalDateTime desde,
                                 @Param("hasta") LocalDateTime hasta);
}
