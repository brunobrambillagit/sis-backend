package com.example.backend_sis.service;

import com.example.backend_sis.exception.BusinessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.List;

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
}
