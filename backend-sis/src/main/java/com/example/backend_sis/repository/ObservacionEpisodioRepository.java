package com.example.backend_sis.repository;

import com.example.backend_sis.model.ObservacionEpisodio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservacionEpisodioRepository extends JpaRepository<ObservacionEpisodio, Long> {

    List<ObservacionEpisodio> findByEpisodioIdOrderByFechaRegistroDesc(Long episodioId);
}
