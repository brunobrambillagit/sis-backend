package com.example.backend_sis.repository;

import com.example.backend_sis.model.EvolucionEpisodio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvolucionEpisodioRepository extends JpaRepository<EvolucionEpisodio, Long> {

    List<EvolucionEpisodio> findByEpisodioIdOrderByFechaRegistroDesc(Long episodioId);
}
