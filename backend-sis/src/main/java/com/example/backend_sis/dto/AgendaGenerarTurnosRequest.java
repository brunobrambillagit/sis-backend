package com.example.backend_sis.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AgendaGenerarTurnosRequest {
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
}
