package com.example.backend_sis.controller;

import com.example.backend_sis.dto.*;
import com.example.backend_sis.service.HuellaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/huellas")
@RequiredArgsConstructor
public class HuellaController {

    private final HuellaService huellaService;

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public HuellaRegistroResponse registrarHuella(@Valid @RequestBody HuellaRegistroRequest request) {
        return huellaService.registrarHuella(request);
    }

    @PostMapping("/buscar")
    public PacienteHuellaMatchResponse buscarPacientePorHuella(@Valid @RequestBody HuellaBusquedaRequest request) {
        return huellaService.buscarPacientePorHuella(request);
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<HuellaPacienteItemResponse> listarHuellasPorPaciente(@PathVariable Long pacienteId) {
        return huellaService.listarHuellasPorPaciente(pacienteId);
    }

    @GetMapping("/admin")
    public List<HuellaAdminItemResponse> listarHuellasAdmin() {
        return huellaService.listarHuellasAdmin();
    }

    @DeleteMapping("/{reconocimientoHuellaId}")
    public ResponseEntity<String> eliminarHuella(@PathVariable Long reconocimientoHuellaId) {
        huellaService.eliminarHuella(reconocimientoHuellaId);
        return ResponseEntity.ok("Huella desactivada correctamente");
    }
}
