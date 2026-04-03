package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReconocimientoResponse {
    private Long id;
    private Long pacienteId;
    private String pacienteDni;
    private String pacienteNombreCompleto;
    private String faceId;
    private String externalImageId;
    private String collectionId;
    private String s3Bucket;
    private String s3Key;
    private String previewUrl;
}
