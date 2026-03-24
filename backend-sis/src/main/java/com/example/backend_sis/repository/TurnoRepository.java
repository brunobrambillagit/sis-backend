package com.example.backend_sis.repository;

import com.example.backend_sis.model.EstadoTurno;
import com.example.backend_sis.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    boolean existsByAgenda_IdAndFechaAndHoraDesde(Long agendaId, LocalDate fecha, LocalTime horaDesde);

    List<Turno> findByAgenda_IdAndFechaOrderByHoraDesdeAsc(Long agendaId, LocalDate fecha);

    List<Turno> findByFechaOrderByHoraDesdeAsc(LocalDate fecha);

    List<Turno> findByFechaAndEstadoTurnoInOrderByHoraDesdeAsc(LocalDate fecha, Collection<EstadoTurno> estados);

    @Query("""
            select t
            from Turno t
            join AgendaPermisoMedico ap on ap.agenda.id = t.agenda.id
            where ap.usuario.id = :usuarioId
              and ap.activo = true
              and t.fecha = :fecha
            order by t.horaDesde asc
            """)
    List<Turno> findTurnosVisiblesPorMedicoYFecha(
            @Param("usuarioId") Long usuarioId,
            @Param("fecha") LocalDate fecha
    );
}
