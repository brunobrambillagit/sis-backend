package com.example.backend_sis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvolucionEpisodioCreateRequest {
    private Long usuarioId;
    private String diagnosticos;
    private String evolucion;
    private String medicacionIndicaciones;
    private String estudiosSolicitados;
}
