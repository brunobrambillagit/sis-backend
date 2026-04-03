package com.example.backend_sis.controller;

import com.example.backend_sis.dto.*;
import com.example.backend_sis.model.Usuario;
import com.example.backend_sis.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody UsuarioRequest request) {
        try {
            Usuario usuario = usuarioService.registrarUsuario(request);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        try {
            return ResponseEntity.ok(usuarioService.listarUsuariosSinAdmin());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long usuarioId,
            @RequestBody UsuarioAdminUpdateRequest request
    ) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarUsuarioAdmin(usuarioId, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{usuarioId}/password")
    public ResponseEntity<?> cambiarPasswordAdmin(
            @PathVariable Long usuarioId,
            @RequestBody UsuarioAdminCambiarPasswordRequest request
    ) {
        try {
            usuarioService.cambiarPasswordAdmin(usuarioId, request.getPasswordNueva());
            return ResponseEntity.ok("Contraseña actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{usuarioId}/reset-password")
    public ResponseEntity<?> reiniciarPassword(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(usuarioService.reiniciarPasswordAdmin(usuarioId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/medicos")
    public ResponseEntity<?> listarMedicos() {
        try {
            return ResponseEntity.ok(usuarioService.listarMedicos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> cambiarPassword(
            @RequestBody CambiarPasswordRequest request,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();

            usuarioService.cambiarPassword(
                    email,
                    request.passwordActual,
                    request.passwordNueva
            );

            return ResponseEntity.ok("Contraseña actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
