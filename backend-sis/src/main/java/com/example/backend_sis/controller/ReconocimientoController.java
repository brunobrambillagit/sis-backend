package com.example.backend_sis.controller;

import com.example.backend_sis.dto.PacienteRostroMatchResponse;
import com.example.backend_sis.dto.ReconocimientoAdminItemResponse;
import com.example.backend_sis.dto.ReconocimientoRostroResponse;
import com.example.backend_sis.service.ReconocimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reconocimiento")
@RequiredArgsConstructor
public class ReconocimientoController {

    private final ReconocimientoService reconocimientoService;

    @PostMapping(value = "/rostro/buscar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PacienteRostroMatchResponse buscarPacientePorRostro(
            @RequestPart("archivo") MultipartFile archivo
    ) {
        return reconocimientoService.buscarPacientePorRostro(archivo);
    }

    @PostMapping(value = "/rostro/{pacienteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ReconocimientoRostroResponse registrarRostro(
            @PathVariable Long pacienteId,
            @RequestPart("archivo") MultipartFile archivo
    ) {
        return reconocimientoService.registrarRostro(pacienteId, archivo);
    }

    @GetMapping("/rostros/admin")
    public List<ReconocimientoAdminItemResponse> listarRostrosAdmin() {
        return reconocimientoService.listarRostrosAdmin();
    }

    @DeleteMapping("/rostros/{reconocimientoId}")
    public Object eliminarRostroAdmin(@PathVariable Long reconocimientoId) {
        reconocimientoService.eliminarRostroAdmin(reconocimientoId);
        return "Registro biométrico eliminado correctamente";
    }
}
