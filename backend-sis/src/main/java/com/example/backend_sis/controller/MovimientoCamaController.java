package com.example.backend_sis.controller;

import com.example.backend_sis.dto.MovimientoCamaResponse;
import com.example.backend_sis.service.MovimientoCamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/episodios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MovimientoCamaController {

    private final MovimientoCamaService movimientoCamaService;

    @GetMapping("/{episodioId}/movimientos-cama")
    public List<MovimientoCamaResponse> obtenerMovimientos(@PathVariable Long episodioId) {
        return movimientoCamaService.obtenerPorEpisodio(episodioId);
    }
}