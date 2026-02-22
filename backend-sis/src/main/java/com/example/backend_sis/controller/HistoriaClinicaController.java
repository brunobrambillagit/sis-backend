package com.example.backend_sis.controller;

import com.example.backend_sis.dto.HistoriaClinicaResponse;
import com.example.backend_sis.exception.BusinessException;
import com.example.backend_sis.model.HistoriaClinica;
import com.example.backend_sis.model.Paciente;
import com.example.backend_sis.repository.HistoriaClinicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historias-clinicas")
@RequiredArgsConstructor
public class HistoriaClinicaController {

    private final HistoriaClinicaRepository historiaClinicaRepository;

    // US-11: Ver historia clínica por DNI (solo MEDICO)
    @GetMapping("/{dni}")
    public HistoriaClinicaResponse verPorDni(@PathVariable String dni) {

        String limpio = (dni == null) ? "" : dni.replaceAll("[^0-9]", "");
        if (limpio.isBlank()) throw new BusinessException("DNI inválido");

        HistoriaClinica hc = historiaClinicaRepository.findByPaciente_Dni(limpio)
                .orElseThrow(() -> new BusinessException("No existe historia clínica para DNI " + limpio));

        // Importante: accedemos a Paciente dentro del request (transacción no garantizada aquí),
        // pero como es OneToOne y estamos en el mismo contexto de persistencia habitual,
        Paciente p = hc.getPaciente();

        return HistoriaClinicaResponse.builder()
                .id(hc.getId())
                .fechaCreacion(hc.getFechaCreacion())
                .observaciones(hc.getObservaciones())
                .paciente(HistoriaClinicaResponse.PacienteResumen.builder()
                        .id(p.getId())
                        .dni(p.getDni())
                        .nombre(p.getNombre())
                        .apellido(p.getApellido())
                        .nroHistoriaClinica(p.getNroHistoriaClinica())
                        .build())
                .build();
    }
}