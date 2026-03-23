package com.example.backend_sis.dto;

import com.example.backend_sis.model.EstadoAtencion;
import com.example.backend_sis.model.TipoServicio;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EpisodioListItemResponse {
    private Long episodioId;
    private Long pacienteId;
    private String dni;
    private String nombre;
    private String apellido;
    private TipoServicio tipoServicio;
    private EstadoAtencion estadoAtencion;
    private LocalDateTime fechaIngreso;
    private Long camaId;
    private String camaCodigo;
}
