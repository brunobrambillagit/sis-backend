package com.example.backend_sis.controller;

import com.example.backend_sis.dto.AgendaCreateRequest;
import com.example.backend_sis.dto.AgendaGenerarTurnosRequest;
import com.example.backend_sis.dto.AgendaResponse;
import com.example.backend_sis.dto.TurnoListItemResponse;
import com.example.backend_sis.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    public ResponseEntity<AgendaResponse> crearAgenda(@RequestBody AgendaCreateRequest request) {
        return ResponseEntity.ok(agendaService.crearAgenda(request));
    }

    @GetMapping
    public ResponseEntity<List<AgendaResponse>> listarAgendas() {
        return ResponseEntity.ok(agendaService.listarAgendas());
    }

    @GetMapping("/medico/{usuarioId}")
    public ResponseEntity<List<AgendaResponse>> listarAgendasDeMedico(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(agendaService.listarAgendasDeMedico(usuarioId));
    }

    @PostMapping("/{agendaId}/generar-turnos")
    public ResponseEntity<List<TurnoListItemResponse>> generarTurnos(
            @PathVariable Long agendaId,
            @RequestBody AgendaGenerarTurnosRequest request
    ) {
        return ResponseEntity.ok(agendaService.generarTurnos(agendaId, request));
    }
}