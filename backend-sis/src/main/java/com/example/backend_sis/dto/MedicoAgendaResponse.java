package com.example.backend_sis.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MedicoAgendaResponse {
    private Long usuarioId;
    private String nombreCompleto;
    private String email;
}
