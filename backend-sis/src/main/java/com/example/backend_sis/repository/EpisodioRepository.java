package com.example.backend_sis.repository;
import com.example.backend_sis.model.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend_sis.model.Episodio;
import com.example.backend_sis.model.EstadoAtencion;
import com.example.backend_sis.model.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
public interface EpisodioRepository extends JpaRepository<Episodio, Long> {

    List<Episodio> findByTipoServicioAndEstadoAtencionInOrderByFechaIngresoDesc(
            TipoServicio tipoServicio,
            Collection<EstadoAtencion> estados
    );

    boolean existsByPaciente_IdAndEstadoAtencionIn(
            Long pacienteId,
            Collection<EstadoAtencion> estados
    );
}