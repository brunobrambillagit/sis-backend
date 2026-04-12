package com.example.backend_sis.controller;

import com.example.backend_sis.dto.*;
import com.example.backend_sis.service.EpisodioDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/episodios")
@RequiredArgsConstructor
public class EpisodioDetalleController {

    private final EpisodioDetalleService episodioDetalleService;

    @GetMapping("/{id}")
    public ResponseEntity<EpisodioDetalleResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(episodioDetalleService.obtenerDetalle(id));
    }

    @PostMapping("/{id}/historia-clinica/historial-observaciones")
    public ResponseEntity<ObservacionHistoriaClinicaResponse> agregarObservacionHistoriaClinica(
            @PathVariable Long id,
            @RequestBody ObservacionHistoriaClinicaCreateRequest request
    ) {
        return ResponseEntity.ok(
                episodioDetalleService.agregarObservacionHistoriaClinica(id, request)
        );
    }

    @PostMapping("/{id}/observaciones")
    public ResponseEntity<ObservacionEpisodioResponse> agregarObservacion(
            @PathVariable Long id,
            @RequestBody ObservacionEpisodioCreateRequest request
    ) {
        return ResponseEntity.ok(episodioDetalleService.agregarObservacion(id, request));
    }

    @PostMapping("/{id}/evoluciones")
    public ResponseEntity<EvolucionEpisodioResponse> agregarEvolucion(
            @PathVariable Long id,
            @RequestBody EvolucionEpisodioCreateRequest request
    ) {
        return ResponseEntity.ok(episodioDetalleService.agregarEvolucion(id, request));
    }

    @PutMapping("/{id}/historia-clinica/observaciones")
    public ResponseEntity<HistoriaClinicaResponse> actualizarObservacionesHC(
            @PathVariable Long id,
            @RequestBody HistoriaClinicaUpdateObservacionesRequest request
    ) {
        return ResponseEntity.ok(
                episodioDetalleService.actualizarObservacionesHC(id, request)
        );
    }
}
