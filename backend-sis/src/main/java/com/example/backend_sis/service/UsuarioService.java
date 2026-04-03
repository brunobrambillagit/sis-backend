package com.example.backend_sis.service;

import com.example.backend_sis.dto.*;
import com.example.backend_sis.model.Usuario;
import com.example.backend_sis.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class UsuarioService {

    private static final String CARACTERES_PASSWORD = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrarUsuario(UsuarioRequest request) {
        validarRolCreable(request.rol);
        validarDuplicados(request.cuit, request.email, null);

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre);
        usuario.setApellido(request.apellido);
        usuario.setCuit(request.cuit);
        usuario.setEmail(request.email);
        usuario.setRol(Usuario.Rol.valueOf(request.rol.toUpperCase()));
        usuario.setPassword(passwordEncoder.encode(request.password));
        usuario.setDebeCambiarPassword(false);

        return usuarioRepository.save(usuario);
    }

    public List<UsuarioMedicoOptionResponse> listarMedicos() {
        return usuarioRepository.findByRol(Usuario.Rol.MEDICO)
                .stream()
                .map(u -> UsuarioMedicoOptionResponse.builder()
                        .id(u.getId())
                        .nombreCompleto((u.getNombre() == null ? "" : u.getNombre()) +
                                (u.getApellido() == null ? "" : " " + u.getApellido()))
                        .email(u.getEmail())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioAdminListResponse> listarUsuariosAdmin() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional
    public UsuarioAdminListResponse actualizarUsuarioAdmin(Long usuarioId, UsuarioAdminUpdateRequest request) {
        validarRolCreable(request.getRol());
        validarDuplicados(request.getCuit(), request.getEmail(), usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() == Usuario.Rol.ADMIN) {
            throw new RuntimeException("No se permite modificar usuarios ADMIN desde este módulo.");
        }

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setCuit(request.getCuit());
        usuario.setEmail(request.getEmail());
        usuario.setRol(Usuario.Rol.valueOf(request.getRol().toUpperCase()));

        return toAdminResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void cambiarPassword(String email, String actual, String nueva) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(actual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(nueva));
        usuario.setDebeCambiarPassword(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPasswordAdmin(Long usuarioId, String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.isBlank()) {
            throw new RuntimeException("Debe informar la nueva contraseña.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() == Usuario.Rol.ADMIN) {
            throw new RuntimeException("No se permite cambiar contraseñas de usuarios ADMIN desde este módulo.");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setDebeCambiarPassword(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioAdminResetPasswordResponse reiniciarPasswordAdmin(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() == Usuario.Rol.ADMIN) {
            throw new RuntimeException("No se permite reiniciar contraseñas de usuarios ADMIN desde este módulo.");
        }

        String passwordTemporal = generarPasswordTemporal(10);
        usuario.setPassword(passwordEncoder.encode(passwordTemporal));
        usuario.setDebeCambiarPassword(true);
        usuarioRepository.save(usuario);

        return UsuarioAdminResetPasswordResponse.builder()
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .passwordTemporal(passwordTemporal)
                .debeCambiarPassword(true)
                .build();
    }

    private void validarRolCreable(String rol) {
        if (rol == null || rol.isBlank()) {
            throw new RuntimeException("El rol es obligatorio.");
        }

        String rolNormalizado = rol.toUpperCase();
        if (!rolNormalizado.equals("MEDICO") && !rolNormalizado.equals("ADMINISTRATIVO")) {
            throw new RuntimeException("Solo se permite crear o modificar usuarios MEDICO o ADMINISTRATIVO.");
        }
    }

    private void validarDuplicados(String cuit, String email, Long usuarioIdActual) {
        usuarioRepository.findByCuit(cuit).ifPresent(u -> {
            if (usuarioIdActual == null || !u.getId().equals(usuarioIdActual)) {
                throw new RuntimeException("CUIT ya existente");
            }
        });

        usuarioRepository.findByEmail(email).ifPresent(u -> {
            if (usuarioIdActual == null || !u.getId().equals(usuarioIdActual)) {
                throw new RuntimeException("Email ya existente");
            }
        });
    }

    private UsuarioAdminListResponse toAdminResponse(Usuario usuario) {
        return UsuarioAdminListResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .cuit(usuario.getCuit())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .debeCambiarPassword(Boolean.TRUE.equals(usuario.getDebeCambiarPassword()))
                .build();
    }

    private String generarPasswordTemporal(int longitud) {
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            int index = secureRandom.nextInt(CARACTERES_PASSWORD.length());
            sb.append(CARACTERES_PASSWORD.charAt(index));
        }
        return sb.toString();
    }

    public List<Usuario> listarUsuariosSinAdmin() {
        return usuarioRepository.findByRolNot(Usuario.Rol.ADMIN);
    }
}
