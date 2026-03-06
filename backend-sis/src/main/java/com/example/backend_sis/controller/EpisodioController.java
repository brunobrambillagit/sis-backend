package com.example.backend_sis.controller;

import com.example.backend_sis.dto.EpisodioCreateRequest;
import com.example.backend_sis.dto.EpisodioListItemResponse;
import com.example.backend_sis.dto.EpisodioUpdateEstadoRequest;
import com.example.backend_sis.model.Episodio;
import com.example.backend_sis.model.EstadoAtencion;
import com.example.backend_sis.model.TipoServicio;
import com.example.backend_sis.service.EpisodioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/episodios")
@RequiredArgsConstructor
public class EpisodioController {

    private final EpisodioService episodioService;

    @PostMapping
    public ResponseEntity<Episodio> crearEpisodio(@RequestBody EpisodioCreateRequest request) {
        return ResponseEntity.ok(episodioService.crearEpisodio(request));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EpisodioListItemResponse>> listarActivos(
            @RequestParam TipoServicio servicio
    ) {
        return ResponseEntity.ok(episodioService.listarActivosPorServicio(servicio));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Episodio> cambiarEstado(
            @PathVariable Long id,
            @RequestBody EpisodioUpdateEstadoRequest request
    ) {
        EstadoAtencion nuevoEstado = request.getNuevoEstado();
        Long usuarioId = request.getUsuarioId();

        return ResponseEntity.ok(
                episodioService.cambiarEstado(id, nuevoEstado, usuarioId)
        );
    }
}