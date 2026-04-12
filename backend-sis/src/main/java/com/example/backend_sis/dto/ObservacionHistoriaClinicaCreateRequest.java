package com.example.backend_sis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObservacionHistoriaClinicaCreateRequest {
    private Long usuarioId;
    private String observacion;
}