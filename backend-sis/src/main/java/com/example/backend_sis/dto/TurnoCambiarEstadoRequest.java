package com.example.backend_sis.dto;

import com.example.backend_sis.model.EstadoTurno;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnoCambiarEstadoRequest {
    private EstadoTurno nuevoEstado;
    private Long usuarioId;
}
