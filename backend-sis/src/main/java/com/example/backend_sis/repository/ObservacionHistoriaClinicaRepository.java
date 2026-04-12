package com.example.backend_sis.repository;

import com.example.backend_sis.model.ObservacionHistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservacionHistoriaClinicaRepository
        extends JpaRepository<ObservacionHistoriaClinica, Long> {

    List<ObservacionHistoriaClinica> findByHistoriaClinicaIdOrderByFechaRegistroDesc(Long historiaClinicaId);
}