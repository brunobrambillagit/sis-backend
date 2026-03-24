package com.example.backend_sis.dto;

import com.example.backend_sis.model.EstadoTurno;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class TurnoListItemResponse {
    private Long turnoId;
    private Long agendaId;
    private String agendaNombre;
    private String especialidad;
    private LocalDate fecha;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
    private EstadoTurno estadoTurno;
    private Long pacienteId;
    private String pacienteDni;
    private String pacienteNombre;
    private String pacienteApellido;
    private Long episodioId;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaLlegada;
    private String observacion;
}