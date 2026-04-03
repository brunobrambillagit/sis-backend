package com.example.backend_sis.controller;

import com.example.backend_sis.dto.CamaAdminRequest;
import com.example.backend_sis.dto.CamaResponse;
import com.example.backend_sis.service.CamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/camas")
@RequiredArgsConstructor
public class CamaController {

    private final CamaService camaService;

    @GetMapping
    public ResponseEntity<?> listarTodas() {
        try {
            return ResponseEntity.ok(camaService.listarTodas());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CamaAdminRequest request) {
        try {
            return ResponseEntity.ok(camaService.crear(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{camaId}")
    public ResponseEntity<?> actualizar(@PathVariable Long camaId, @RequestBody CamaAdminRequest request) {
        try {
            return ResponseEntity.ok(camaService.actualizar(camaId, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/disponibles/hospitalizacion")
    public ResponseEntity<List<CamaResponse>> listarDisponiblesHospitalizacion() {
        return ResponseEntity.ok(camaService.listarDisponiblesHospitalizacion());
    }
}
