package com.example.backend_sis.dto;

import com.example.backend_sis.model.EstadoAtencion;
import com.example.backend_sis.model.TipoServicio;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class EpisodioDetalleResponse {
    private Long episodioId;
    private TipoServicio tipoServicio;
    private EstadoAtencion estadoAtencion;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaEgreso;

    private Long pacienteId;
    private String dni;
    private String nombre;
    private String apellido;
    private String nroHistoriaClinica;

    private Long camaId;
    private String camaCodigo;

    private String observacionesHistoriaClinica;

    private List<ObservacionEpisodioResponse> observaciones;
    private List<EvolucionEpisodioResponse> evoluciones;
    private List<ObservacionHistoriaClinicaResponse> historialObservacionesHistoriaClinica;
}