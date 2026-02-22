package com.example.backend_sis.repository;

import com.example.backend_sis.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByDni(String dni);

    // Búsqueda simple tipo “teclado” (US-9)
    List<Paciente> findTop20ByDniContainingIgnoreCaseOrNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String dni, String nombre, String apellido
    );
}