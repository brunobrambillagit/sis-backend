package com.example.backend_sis.dto;

import com.example.backend_sis.model.Reconocimiento;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ReconocimientoRostroResponse {

    private Long id;
    private Long pacienteId;
    private String pacienteDni;
    private String pacienteNombreCompleto;
    private Reconocimiento.TipoBiometria tipoBiometria;
    private String proveedor;
    private String referenciaBiometrica;
    private String collectionId;
    private String externalImageId;
    private String s3Bucket;
    private String s3Key;
    private Date fechaCaptura;
    private Date fechaVinculoPaciente;
}

