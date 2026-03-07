package com.example.backend_sis.service;

import com.example.backend_sis.dto.LoginRequest;
import com.example.backend_sis.dto.LoginResponse;
import com.example.backend_sis.model.Usuario;
import com.example.backend_sis.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
                       UsuarioDetailsService usuarioDetailsService,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioDetailsService = usuarioDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {

        Usuario u = usuarioRepository.findByEmail(request.email)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password, u.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(u.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                u.getId(),
                token,
                u.getEmail(),
                u.getRol().name()
        );
    }
}