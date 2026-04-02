package com.example.backend_sis.service;

import com.example.backend_sis.dto.UsuarioMedicoOptionResponse;
import com.example.backend_sis.dto.UsuarioRequest;
import com.example.backend_sis.model.Usuario;
import com.example.backend_sis.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrarUsuario(UsuarioRequest request) {

        if (usuarioRepository.findByCuit(request.cuit).isPresent()) {
            throw new RuntimeException("CUIT ya existente");
        }

        if (usuarioRepository.findByEmail(request.email).isPresent()) {
            throw new RuntimeException("Email ya existente");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre);
        usuario.setApellido(request.apellido);
        usuario.setCuit(request.cuit);
        usuario.setEmail(request.email);
        usuario.setRol(Usuario.Rol.valueOf(request.rol.toUpperCase()));
        usuario.setPassword(passwordEncoder.encode(request.password));

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

    public void cambiarPassword(String email, String actual, String nueva) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(actual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }
}
