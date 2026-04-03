package com.example.backend_sis.repository;

import com.example.backend_sis.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCuit(String cuit);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRolNot(Usuario.Rol rol);

    List<Usuario> findByRol(Usuario.Rol rol);
}
