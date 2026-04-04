package com.example.backend_sis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReconocimientoAdminItemResponse {

    private Long id;
    private Long pacienteId;
    private String pacienteDni;
    private String pacienteNombreCompleto;
    private String faceId;
    private String externalImageId;
    private String s3Bucket;
    private String s3Key;
    private String collectionId;
    private String previewUrl;
}
