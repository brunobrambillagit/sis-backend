package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ObservacionEpisodioResponse {
    private Long id;
    private String observacion;
    private LocalDateTime fechaRegistro;
    private Long usuarioId;
    private String nombreUsuario;
}
