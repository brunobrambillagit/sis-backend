package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class TurnoComprobanteResponse {
    private Long turnoId;
    private String agendaNombre;
    private String especialidad;
    private LocalDate fecha;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
    private String pacienteDni;
    private String pacienteNombreCompleto;
    private String observacion;
}
