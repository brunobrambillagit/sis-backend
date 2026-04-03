package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsuarioAdminResetPasswordResponse {
    private Long usuarioId;
    private String email;
    private String passwordTemporal;
    private Boolean debeCambiarPassword;
}
