package com.example.backend_sis.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class AgendaDiaAtencionRequest {
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
