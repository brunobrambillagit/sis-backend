package com.example.backend_sis.service;

import com.example.backend_sis.dto.ReconocimientoRostroResponse;
import com.example.backend_sis.exception.BusinessException;
import com.example.backend_sis.model.Paciente;
import com.example.backend_sis.model.Reconocimiento;
import com.example.backend_sis.repository.PacienteRepository;
import com.example.backend_sis.repository.ReconocimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReconocimientoService {

    private static final String PROVEEDOR_AWS_REKOGNITION = "AWS_REKOGNITION";

    private final PacienteRepository pacienteRepository;
    private final ReconocimientoRepository reconocimientoRepository;
    private final S3Service s3Service;
    private final RekognitionService rekognitionService;

    public ReconocimientoRostroResponse registrarRostro(Long pacienteId, MultipartFile archivo) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new BusinessException("No existe paciente con id " + pacienteId));

        boolean yaTieneRostro = reconocimientoRepository.existsByPacienteIdAndTipoBiometria(
                pacienteId,
                Reconocimiento.TipoBiometria.ROSTRO
        );

        if (yaTieneRostro) {
            throw new BusinessException("El paciente ya tiene un rostro registrado.");
        }

        S3Service.UploadResult uploadResult = s3Service.subirRostro(archivo, pacienteId);

        String externalImageId = "paciente-" + paciente.getId() + "-" + paciente.getDni();

        RekognitionService.IndexFaceResult indexFaceResult = rekognitionService.indexarRostro(
                uploadResult.getBucket(),
                uploadResult.getKey(),
                externalImageId
        );

        Date ahora = new Date();

        Reconocimiento reconocimiento = new Reconocimiento();
        reconocimiento.setReferenciaBiometrica(indexFaceResult.getFaceId());
        reconocimiento.setTipoBiometria(Reconocimiento.TipoBiometria.ROSTRO);
        reconocimiento.setProveedor(PROVEEDOR_AWS_REKOGNITION);
        reconocimiento.setCollectionId(indexFaceResult.getCollectionId());
        reconocimiento.setExternalImageId(indexFaceResult.getExternalImageId());
        reconocimiento.setS3Bucket(uploadResult.getBucket());
        reconocimiento.setS3Key(uploadResult.getKey());
        reconocimiento.setFechaCaptura(ahora);
        reconocimiento.setFechaVinculoPaciente(ahora);
        reconocimiento.setPaciente(paciente);

        Reconocimiento guardado = reconocimientoRepository.save(reconocimiento);

        return toResponse(guardado);
    }

    private ReconocimientoRostroResponse toResponse(Reconocimiento r) {
        Paciente p = r.getPaciente();

        return ReconocimientoRostroResponse.builder()
                .id(r.getId())
                .pacienteId(p.getId())
                .pacienteDni(p.getDni())
                .pacienteNombreCompleto((p.getApellido() == null ? "" : p.getApellido()) + ", " + (p.getNombre() == null ? "" : p.getNombre()))
                .tipoBiometria(r.getTipoBiometria())
                .proveedor(r.getProveedor())
                .referenciaBiometrica(r.getReferenciaBiometrica())
                .collectionId(r.getCollectionId())
                .externalImageId(r.getExternalImageId())
                .s3Bucket(r.getS3Bucket())
                .s3Key(r.getS3Key())
                .fechaCaptura(r.getFechaCaptura())
                .fechaVinculoPaciente(r.getFechaVinculoPaciente())
                .build();
    }
}
