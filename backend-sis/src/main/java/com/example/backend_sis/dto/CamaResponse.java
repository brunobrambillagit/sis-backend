package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CamaResponse {
    private Long id;
    private String codigo;
    private String descripcion;
    private String estado;
}
