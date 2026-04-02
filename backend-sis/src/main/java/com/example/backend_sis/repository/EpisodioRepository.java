package com.example.backend_sis.repository;
import com.example.backend_sis.model.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend_sis.model.EstadoAtencion;
import com.example.backend_sis.model.TipoServicio;


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

    List<Episodio> findByPaciente_DniOrderByFechaIngresoDesc(String dni);
}