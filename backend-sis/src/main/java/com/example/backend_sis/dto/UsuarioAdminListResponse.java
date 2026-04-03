package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsuarioAdminListResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String cuit;
    private String email;
    private String rol;
    private Boolean debeCambiarPassword;
}
