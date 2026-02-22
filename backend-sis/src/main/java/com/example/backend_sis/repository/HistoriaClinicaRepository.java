package com.example.backend_sis.repository;

import com.example.backend_sis.model.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

    Optional<HistoriaClinica> findByPaciente_Dni(String dni);

    Optional<HistoriaClinica> findByPaciente_Id(Long pacienteId);
}