package com.example.backend_sis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CamaAdminRequest {
    private String codigo;
    private String descripcion;
    private String tipoServicio;
}
