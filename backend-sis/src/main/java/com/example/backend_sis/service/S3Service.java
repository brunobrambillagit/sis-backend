package com.example.backend_sis.service;

import com.example.backend_sis.exception.BusinessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public UploadResult subirRostro(MultipartFile archivo, Long pacienteId) {
        validarArchivoImagen(archivo);

        String extension = obtenerExtension(archivo.getOriginalFilename());
        String key = "pacientes/" + pacienteId + "/rostro/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(archivo.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(archivo.getBytes()));

            return new UploadResult(bucketName, key);
        } catch (IOException e) {
            throw new BusinessException("No se pudo leer el archivo de rostro.");
        } catch (Exception e) {
            throw new BusinessException("No se pudo subir la imagen a S3: " + e.getMessage());
        }
    }

    public void eliminarObjeto(String bucket, String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new BusinessException("No se pudo eliminar la imagen de S3: " + e.getMessage());
        }
    }

    public String generarUrlTemporal(String bucket, String key, int minutos) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(minutos))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            throw new BusinessException("No se pudo generar la URL temporal de la imagen: " + e.getMessage());
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

    private String obtenerExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    @Getter
    public static class UploadResult {
        private final String bucket;
        private final String key;

        public UploadResult(String bucket, String key) {
            this.bucket = bucket;
            this.key = key;
        }
    }
}