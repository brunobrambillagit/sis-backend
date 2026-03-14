package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EvolucionEpisodioResponse {
    private Long id;
    private String diagnosticos;
    private String evolucion;
    private String medicacionIndicaciones;
    private String estudiosSolicitados;
    private LocalDateTime fechaRegistro;
    private Long usuarioId;
    private String nombreUsuario;
}
