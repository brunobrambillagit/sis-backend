package com.example.backend_sis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AgendaCreateRequest {
    private String nombre;
    private String especialidad;
    private Integer duracionTurnoMinutos;
    private List<AgendaDiaAtencionRequest> diasAtencion;
    private List<Long> medicoIdsConPermiso;
}
