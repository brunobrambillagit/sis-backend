package com.example.backend_sis.controller;

import com.example.backend_sis.dto.PacienteCreateRequest;
import com.example.backend_sis.dto.PacienteResponse;
import com.example.backend_sis.model.Paciente;
import com.example.backend_sis.repository.PacienteRepository;
import com.example.backend_sis.service.AdmisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final AdmisionService admisionService;
    private final PacienteRepository pacienteRepository;

// US-2 + US-10: Alta paciente + creación automática HC
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public PacienteResponse crear(@Valid @RequestBody PacienteCreateRequest req) {
    return admisionService.registrarPaciente(req);
}

// US-9: búsqueda simple por teclado (dni/nombre/apellido)
@GetMapping("/buscar")
public List<PacienteResponse> buscar(@RequestParam("q") String q) {
    String term = (q == null) ? "" : q.trim();

    List<Paciente> encontrados =
            pacienteRepository.findTop20ByDniContainingIgnoreCaseOrNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                    term, term, term
            );

    return encontrados.stream()
            .map(p -> PacienteResponse.builder()
                    .id(p.getId())
                    .dni(p.getDni())
                    .nombre(p.getNombre())
                    .apellido(p.getApellido())
                    .nroHistoriaClinica(p.getNroHistoriaClinica())
                    .build())
            .toList();
}

@GetMapping("/{dni}")
public PacienteResponse obtenerPorDni(@PathVariable String dni) {
    String limpio = (dni == null) ? "" : dni.replaceAll("[^0-9]", "");
    if (limpio.isBlank()) {
        throw new com.example.backend_sis.exception.BusinessException("DNI inválido");
    }

    Paciente p = pacienteRepository.findByDni(limpio)
            .orElseThrow(() -> new com.example.backend_sis.exception.BusinessException(
                    "No existe paciente con DNI " + limpio
            ));

    return PacienteResponse.builder()
            .id(p.getId())
            .dni(p.getDni())
            .nombre(p.getNombre())
            .apellido(p.getApellido())
            .nroHistoriaClinica(p.getNroHistoriaClinica())
            .build();
}




}