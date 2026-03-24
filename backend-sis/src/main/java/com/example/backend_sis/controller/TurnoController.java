package com.example.backend_sis.controller;

import com.example.backend_sis.dto.*;
import com.example.backend_sis.model.EstadoTurno;
import com.example.backend_sis.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @PatchMapping("/{turnoId}/asignar-paciente")
    public ResponseEntity<TurnoComprobanteResponse> asignarPaciente(
            @PathVariable Long turnoId,
            @RequestBody TurnoAsignarPacienteRequest request
    ) {
        return ResponseEntity.ok(turnoService.asignarPaciente(turnoId, request));
    }

    @PatchMapping("/{turnoId}/estado")
    public ResponseEntity<TurnoListItemResponse> cambiarEstado(
            @PathVariable Long turnoId,
            @RequestBody TurnoCambiarEstadoRequest request
    ) {
        EstadoTurno nuevoEstado = request.getNuevoEstado();
        Long usuarioId = request.getUsuarioId();
        return ResponseEntity.ok(turnoService.cambiarEstado(turnoId, nuevoEstado, usuarioId));
    }

    @PatchMapping("/{turnoId}/reprogramar")
    public ResponseEntity<TurnoListItemResponse> reprogramarTurno(
            @PathVariable Long turnoId,
            @RequestBody TurnoReprogramarRequest request
    ) {
        return ResponseEntity.ok(turnoService.reprogramarTurno(turnoId, request));
    }

    @GetMapping("/dia")
    public ResponseEntity<List<TurnoListItemResponse>> listarTurnosDelDiaAdmin(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(turnoService.listarTurnosDelDiaAdmin(fecha));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<TurnoListItemResponse>> listarTurnosFiltradosAdmin(
            @RequestParam Long agendaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) EstadoTurno estado
    ) {
        return ResponseEntity.ok(turnoService.listarTurnosFiltradosAdmin(agendaId, fecha, estado));
    }

    @GetMapping("/medico/{usuarioId}")
    public ResponseEntity<List<TurnoListItemResponse>> listarTurnosDelDiaMedico(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(turnoService.listarTurnosDelDiaMedico(usuarioId, fecha));
    }

    @GetMapping("/{turnoId}/comprobante")
    public ResponseEntity<TurnoComprobanteResponse> obtenerComprobante(
            @PathVariable Long turnoId
    ) {
        return ResponseEntity.ok(turnoService.obtenerComprobante(turnoId));
    }
}
