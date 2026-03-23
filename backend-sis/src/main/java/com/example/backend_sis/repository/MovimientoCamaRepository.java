package com.example.backend_sis.repository;

import com.example.backend_sis.model.MovimientoCama;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoCamaRepository extends JpaRepository<MovimientoCama, Long> {

    List<MovimientoCama> findByEpisodioIdOrderByFechaMovimientoAsc(Long episodioId);
}