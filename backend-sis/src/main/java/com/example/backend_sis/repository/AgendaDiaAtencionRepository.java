package com.example.backend_sis.repository;

import com.example.backend_sis.model.AgendaDiaAtencion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaDiaAtencionRepository extends JpaRepository<AgendaDiaAtencion, Long> {
    List<AgendaDiaAtencion> findByAgenda_Id(Long agendaId);
}
