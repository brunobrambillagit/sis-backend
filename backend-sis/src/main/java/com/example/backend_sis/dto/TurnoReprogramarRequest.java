package com.example.backend_sis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnoReprogramarRequest {
    private Long nuevoTurnoId;
    private Long usuarioId;
}
