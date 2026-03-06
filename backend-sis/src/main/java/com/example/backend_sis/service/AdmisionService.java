package com.example.backend_sis.service;

import com.example.backend_sis.dto.PacienteCreateRequest;
import com.example.backend_sis.dto.PacienteResponse;
import com.example.backend_sis.exception.BusinessException;
import com.example.backend_sis.model.HistoriaClinica;
import com.example.backend_sis.model.Paciente;
import com.example.backend_sis.repository.HistoriaClinicaRepository;
import com.example.backend_sis.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AdmisionService {

    private final PacienteRepository pacienteRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;

    @Transactional
    public PacienteResponse registrarPaciente(PacienteCreateRequest req) {

        String dni = normalizarDni(req.getDni());

        if (pacienteRepository.findByDni(dni).isPresent()) {
            throw new BusinessException("Ya existe un paciente con DNI " + dni);
        }

        // N° HC automático
        String nroHC = generarNroHistoriaClinica(dni);

        Paciente paciente = Paciente.builder()
                .dni(dni)
                .nombre(req.getNombre().trim())
                .apellido(req.getApellido().trim())
                .nroHistoriaClinica(nroHC)
                .fechaAlta(new Date())
                .fechaModificacion(new Date())
                // defaults razonables (ajustables)
                .estadoPersona(Paciente.EstadoPersona.VIVO)
                .build();

        Paciente pacienteGuardado = pacienteRepository.save(paciente);

        HistoriaClinica hc = HistoriaClinica.builder()
                .paciente(pacienteGuardado)
                .fechaCreacion(LocalDateTime.now())
                .observaciones(null)
                .build();

        historiaClinicaRepository.save(hc);

        return PacienteResponse.builder()
                .id(pacienteGuardado.getId())
                .dni(pacienteGuardado.getDni())
                .nombre(pacienteGuardado.getNombre())
                .apellido(pacienteGuardado.getApellido())
                .nroHistoriaClinica(pacienteGuardado.getNroHistoriaClinica())
                .build();
    }

    private String generarNroHistoriaClinica(String dni) {
        return "HC-" + dni;
    }

    private String normalizarDni(String dni) {
        String limpio = dni == null ? "" : dni.replaceAll("[^0-9]", "");
        if (limpio.isBlank()) throw new BusinessException("DNI inválido");
        return limpio;
    }
}