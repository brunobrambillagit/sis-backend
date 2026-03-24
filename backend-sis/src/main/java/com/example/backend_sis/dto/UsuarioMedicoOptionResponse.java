package com.example.backend_sis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioMedicoOptionResponse {

    private Long id;
    private String nombreCompleto;
    private String email;
}
