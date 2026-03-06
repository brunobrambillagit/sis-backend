package com.example.backend_sis.dto;

import com.example.backend_sis.model.EstadoAtencion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EpisodioUpdateEstadoRequest {
    private EstadoAtencion nuevoEstado;
    private Long usuarioId;
}