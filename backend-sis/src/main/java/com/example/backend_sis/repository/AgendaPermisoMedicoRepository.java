package com.example.backend_sis.repository;

import com.example.backend_sis.model.AgendaPermisoMedico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaPermisoMedicoRepository extends JpaRepository<AgendaPermisoMedico, Long> {

    boolean existsByAgenda_IdAndUsuario_IdAndActivoTrue(Long agendaId, Long usuarioId);

    List<AgendaPermisoMedico> findByAgenda_IdAndActivoTrue(Long agendaId);

    List<AgendaPermisoMedico> findByUsuario_IdAndActivoTrue(Long usuarioId);
}
