package com.example.backend_sis.dto;

import com.example.backend_sis.model.TipoServicio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EpisodioCreateRequest {
    private Long pacienteId;
    private TipoServicio tipoServicio;
    private Long usuarioId;
    private Long camaId;
}
