package com.example.backend_sis.repository;

import com.example.backend_sis.model.Reconocimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReconocimientoRepository extends JpaRepository<Reconocimiento, Long> {
    boolean existsByPacienteIdAndTipoBiometria(Long pacienteId, Reconocimiento.TipoBiometria tipoBiometria);

    Optional<Reconocimiento> findByPacienteIdAndTipoBiometria(Long pacienteId, Reconocimiento.TipoBiometria tipoBiometria);

    Optional<Reconocimiento> findByReferenciaBiometricaAndTipoBiometria(
            String referenciaBiometrica,
            Reconocimiento.TipoBiometria tipoBiometria
    );

    List<Reconocimiento> findByTipoBiometriaOrderByIdDesc(Reconocimiento.TipoBiometria tipoBiometria);
}
