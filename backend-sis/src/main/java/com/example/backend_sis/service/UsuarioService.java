package com.example.backend_sis.service;

import com.example.backend_sis.dto.UsuarioRequest;
import com.example.backend_sis.model.Usuario;
import com.example.backend_sis.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrarUsuario(UsuarioRequest request) {

        // Validaciones
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

        // Encriptación segura
        usuario.setPassword(passwordEncoder.encode(request.password));

        return usuarioRepository.save(usuario);
    }
}
