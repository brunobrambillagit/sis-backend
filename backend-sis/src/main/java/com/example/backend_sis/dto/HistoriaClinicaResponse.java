package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistoriaClinicaResponse {

    private Long id;
    private LocalDateTime fechaCreacion;
    private String observaciones;

    private PacienteResumen paciente;

    @Getter
    @Builder
    public static class PacienteResumen {
        private Long id;
        private String dni;
        private String nombre;
        private String apellido;
        private String nroHistoriaClinica;
    }
}