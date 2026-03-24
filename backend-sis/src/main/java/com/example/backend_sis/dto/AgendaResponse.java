package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class AgendaResponse {
    private Long id;
    private String nombre;
    private String especialidad;
    private Integer duracionTurnoMinutos;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
    private List<AgendaDiaAtencionResponse> diasAtencion;
    private List<MedicoAgendaResponse> medicosConPermiso;
}
