package com.example.backend_sis.repository;

import com.example.backend_sis.model.DedoHuella;
import com.example.backend_sis.model.ReconocimientoHuella;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReconocimientoHuellaRepository extends JpaRepository<ReconocimientoHuella, Long> {

    boolean existsByPacienteIdAndDedoAndActivoTrue(Long pacienteId, DedoHuella dedo);

    Optional<ReconocimientoHuella> findByPacienteIdAndDedoAndActivoTrue(Long pacienteId, DedoHuella dedo);

    List<ReconocimientoHuella> findByPacienteIdOrderByFechaCapturaDesc(Long pacienteId);

    List<ReconocimientoHuella> findByActivoTrueOrderByIdAsc();
}
