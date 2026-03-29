package com.example.backend_sis.service;

import com.example.backend_sis.exception.BusinessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.model.FaceMatch;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageRequest;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RekognitionService {

    private final RekognitionClient rekognitionClient;

    @Value("${aws.rekognition.collection-id}")
    private String collectionId;

    public IndexFaceResult indexarRostro(String bucket, String key, String externalImageId) {
        asegurarColeccion();

        try {
            Image image = Image.builder()
                    .s3Object(S3Object.builder()
                            .bucket(bucket)
                            .name(key)
                            .build())
                    .build();

            IndexFacesRequest request = IndexFacesRequest.builder()
                    .collectionId(collectionId)
                    .image(image)
                    .externalImageId(externalImageId)
                    .maxFaces(1)
                    .qualityFilter(QualityFilter.AUTO)
                    .build();

            IndexFacesResponse response = rekognitionClient.indexFaces(request);
            List<FaceRecord> faceRecords = response.faceRecords();

            if (faceRecords == null || faceRecords.isEmpty()) {
                throw new BusinessException("No se detectó un rostro válido en la imagen enviada.");
            }

            String faceId = faceRecords.get(0).face().faceId();

            return new IndexFaceResult(faceId, collectionId, externalImageId);
        } catch (RekognitionException e) {
            throw new BusinessException("Error al indexar el rostro en AWS Rekognition: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            throw new BusinessException("Error inesperado al indexar el rostro: " + e.getMessage());
        }
    }

    private void asegurarColeccion() {
        try {
            rekognitionClient.describeCollection(
                    DescribeCollectionRequest.builder()
                            .collectionId(collectionId)
                            .build()
            );
        } catch (ResourceNotFoundException e) {
            try {
                rekognitionClient.createCollection(
                        CreateCollectionRequest.builder()
                                .collectionId(collectionId)
                                .build()
                );
            } catch (ResourceAlreadyExistsException ignored) {
            }
        }
    }

    @Getter
    public static class IndexFaceResult {
        private final String faceId;
        private final String collectionId;
        private final String externalImageId;

        public IndexFaceResult(String faceId, String collectionId, String externalImageId) {
            this.faceId = faceId;
            this.collectionId = collectionId;
            this.externalImageId = externalImageId;
        }
    }

    public SearchFaceResult buscarRostro(MultipartFile archivo) {
        validarArchivoImagen(archivo);

        try {
            Image image = Image.builder()
                    .bytes(SdkBytes.fromByteArray(archivo.getBytes()))
                    .build();

            SearchFacesByImageRequest request = SearchFacesByImageRequest.builder()
                    .collectionId(collectionId)
                    .image(image)
                    .faceMatchThreshold(90F)
                    .maxFaces(1)
                    .build();

            SearchFacesByImageResponse response = rekognitionClient.searchFacesByImage(request);
            List<FaceMatch> matches = response.faceMatches();

            if (matches == null || matches.isEmpty()) {
                throw new BusinessException("No se encontró un paciente registrado con ese rostro.");
            }

            FaceMatch match = matches.get(0);

            if (match.face() == null || match.face().faceId() == null || match.face().faceId().isBlank()) {
                throw new BusinessException("No se encontró una coincidencia válida para el rostro enviado.");
            }

            return new SearchFaceResult(match.face().faceId(), match.similarity());
        } catch (IOException e) {
            throw new BusinessException("No se pudo leer la imagen enviada para buscar por rostro.");
        } catch (RekognitionException e) {
            throw new BusinessException("Error al buscar coincidencias en AWS Rekognition: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            throw new BusinessException("Error inesperado al buscar por rostro: " + e.getMessage());
        }
    }

    private void validarArchivoImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("Debe enviar un archivo de imagen.");
        }

        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new BusinessException("El archivo enviado debe ser una imagen válida.");
        }
    }

    @Getter
    public static class SearchFaceResult {
        private final String faceId;
        private final Float similarity;

        public SearchFaceResult(String faceId, Float similarity) {
            this.faceId = faceId;
            this.similarity = similarity;
        }
    }

}
