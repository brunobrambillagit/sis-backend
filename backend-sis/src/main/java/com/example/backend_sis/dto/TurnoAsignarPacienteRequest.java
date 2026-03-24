package com.example.backend_sis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnoAsignarPacienteRequest {
    private Long pacienteId;
    private String observacion;
}
