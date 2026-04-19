package com.example.backend_sis.dto;

import com.example.backend_sis.model.DedoHuella;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class HuellaRegistroResponse {

    private Long id;
    private Long pacienteId;
    private String pacienteDni;
    private String pacienteNombreCompleto;
    private DedoHuella dedo;
    private Integer width;
    private Integer height;
    private Integer dpi;
    private String quality;
    private String proveedor;
    private Boolean activo;
    private Date fechaCaptura;
}
