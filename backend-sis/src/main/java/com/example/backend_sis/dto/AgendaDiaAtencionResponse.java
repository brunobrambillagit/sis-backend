package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class AgendaDiaAtencionResponse {
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
