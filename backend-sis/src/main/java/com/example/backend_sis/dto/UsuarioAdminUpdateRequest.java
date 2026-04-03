package com.example.backend_sis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioAdminUpdateRequest {
    private String nombre;
    private String apellido;
    private String cuit;
    private String email;
    private String rol;
}
