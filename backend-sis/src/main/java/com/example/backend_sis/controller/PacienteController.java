package com.example.backend_sis.controller;

import com.example.backend_sis.dto.PacienteCreateRequest;
import com.example.backend_sis.dto.PacienteResponse;
import com.example.backend_sis.dto.PacienteUpdateRequest;
import com.example.backend_sis.exception.BusinessException;
import com.example.backend_sis.model.Paciente;
import com.example.backend_sis.repository.PacienteRepository;
import com.example.backend_sis.service.AdmisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{dni}")
    public PacienteResponse obtenerPorDni(@PathVariable String dni) {
        Paciente paciente = buscarPacientePorDni(dni);
        return toResponse(paciente);
    }

    @PutMapping("/{dni}")
    public PacienteResponse actualizarPorDni(@PathVariable String dni,
                                             @Valid @RequestBody PacienteUpdateRequest req) {

        Paciente paciente = buscarPacientePorDni(dni);

        paciente.setNombre(normalizarTexto(req.getNombre()));
        paciente.setApellido(normalizarTexto(req.getApellido()));
        paciente.setFechaNacimiento(req.getFechaNacimiento());
        paciente.setEdad(req.getEdad());
        paciente.setSexo(req.getSexo());
        paciente.setEstadoPersona(req.getEstadoPersona());
        paciente.setFechaModificacion(new Date());

        Paciente actualizado = pacienteRepository.save(paciente);
        return toResponse(actualizado);
    }

    private Paciente buscarPacientePorDni(String dni) {
        String limpio = limpiarDni(dni);

        if (limpio.isBlank()) {
            throw new BusinessException("DNI inválido");
        }

        return pacienteRepository.findByDni(limpio)
                .orElseThrow(() -> new BusinessException("No existe paciente con DNI " + limpio));
    }

    private String limpiarDni(String dni) {
        return (dni == null) ? "" : dni.replaceAll("[^0-9]", "");
    }

    private String normalizarTexto(String valor) {
        return valor == null ? null : valor.trim();
    }

    private PacienteResponse toResponse(Paciente p) {
        return PacienteResponse.builder()
                .id(p.getId())
                .dni(p.getDni())
                .nombre(p.getNombre())
                .apellido(p.getApellido())
                .nroHistoriaClinica(p.getNroHistoriaClinica())
                .fechaNacimiento(p.getFechaNacimiento())
                .edad(p.getEdad())
                .sexo(p.getSexo())
                .estadoPersona(p.getEstadoPersona())
                .fechaAlta(p.getFechaAlta())
                .fechaModificacion(p.getFechaModificacion())
                .build();
    }
}
