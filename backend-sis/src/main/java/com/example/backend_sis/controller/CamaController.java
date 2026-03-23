package com.example.backend_sis.controller;

import com.example.backend_sis.dto.CamaResponse;
import com.example.backend_sis.service.CamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/camas")
@RequiredArgsConstructor
public class CamaController {

    private final CamaService camaService;

    @GetMapping("/disponibles/hospitalizacion")
    public ResponseEntity<List<CamaResponse>> listarDisponiblesHospitalizacion() {
        return ResponseEntity.ok(camaService.listarDisponiblesHospitalizacion());
    }
}
