package com.example.backend_sis.config;

import com.example.backend_sis.model.Usuario;
import com.example.backend_sis.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String emailAdmin = "root@root.com";

        boolean existe = usuarioRepository.findByEmail(emailAdmin).isPresent();

        if (existe) {
            System.out.println("El usuario ADMIN ya existe. No se crea nuevamente.");
            return;
        }

        Usuario admin = Usuario.builder()
                .nombre("Bruno")
                .apellido("Brambilla")
                .cuit("00000000")
                .email(emailAdmin)
                .password(passwordEncoder.encode("Hola1234!"))
                .rol(Usuario.Rol.ADMIN)
                .build();

        usuarioRepository.save(admin);

        System.out.println("✅ Usuario ADMIN inicial creado correctamente.");
        System.out.println("👉 Email: root@root.com");
        System.out.println("👉 Password: Hola1234!");
    }
}