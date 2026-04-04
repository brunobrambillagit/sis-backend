package com.example.backend_sis.service;

import com.example.backend_sis.dto.PacienteRostroMatchResponse;
import com.example.backend_sis.dto.ReconocimientoAdminItemResponse;
import com.example.backend_sis.dto.ReconocimientoRostroResponse;
import com.example.backend_sis.exception.BusinessException;
import com.example.backend_sis.model.Paciente;
import com.example.backend_sis.model.Reconocimiento;
import com.example.backend_sis.repository.PacienteRepository;
import com.example.backend_sis.repository.ReconocimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReconocimientoService {

    private static final String PROVEEDOR_AWS_REKOGNITION = "AWS_REKOGNITION";

    private final PacienteRepository pacienteRepository;
    private final ReconocimientoRepository reconocimientoRepository;
    private final S3Service s3Service;
    private final RekognitionService rekognitionService;

    @Transactional
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

    @Transactional(readOnly = true)
    public List<ReconocimientoAdminItemResponse> listarRostrosAdmin() {
        return reconocimientoRepository.findAll()
                .stream()
                .filter(r -> r.getTipoBiometria() == Reconocimiento.TipoBiometria.ROSTRO)
                .map(this::toAdminItemResponse)
                .toList();
    }

    @Transactional
    public void eliminarRostroAdmin(Long reconocimientoId) {
        Reconocimiento reconocimiento = reconocimientoRepository.findById(reconocimientoId)
                .orElseThrow(() -> new BusinessException("No existe el registro biométrico con id " + reconocimientoId));

        if (reconocimiento.getTipoBiometria() != Reconocimiento.TipoBiometria.ROSTRO) {
            throw new BusinessException("El registro indicado no corresponde a un rostro.");
        }

        if (reconocimiento.getReferenciaBiometrica() != null && !reconocimiento.getReferenciaBiometrica().isBlank()) {
            rekognitionService.eliminarRostro(reconocimiento.getReferenciaBiometrica());
        }

        if (reconocimiento.getS3Bucket() != null && !reconocimiento.getS3Bucket().isBlank()
                && reconocimiento.getS3Key() != null && !reconocimiento.getS3Key().isBlank()) {
            s3Service.eliminarObjeto(reconocimiento.getS3Bucket(), reconocimiento.getS3Key());
        }

        reconocimientoRepository.delete(reconocimiento);
    }

    @Transactional(readOnly = true)
    public PacienteRostroMatchResponse buscarPacientePorRostro(MultipartFile archivo) {
        RekognitionService.SearchFaceResult searchResult = rekognitionService.buscarRostro(archivo);

        Reconocimiento reconocimiento = reconocimientoRepository
                .findByReferenciaBiometricaAndTipoBiometria(
                        searchResult.getFaceId(),
                        Reconocimiento.TipoBiometria.ROSTRO
                )
                .orElseThrow(() -> new BusinessException("Se encontró una coincidencia en Rekognition, pero no está vinculada a un paciente del sistema."));

        Paciente paciente = reconocimiento.getPaciente();

        if (paciente == null) {
            throw new BusinessException("La coincidencia encontrada no tiene un paciente asociado.");
        }

        return PacienteRostroMatchResponse.builder()
                .id(paciente.getId())
                .dni(paciente.getDni())
                .nombre(paciente.getNombre())
                .apellido(paciente.getApellido())
                .nroHistoriaClinica(paciente.getNroHistoriaClinica())
                .fechaNacimiento(paciente.getFechaNacimiento())
                .edad(paciente.getEdad())
                .sexo(paciente.getSexo())
                .estadoPersona(paciente.getEstadoPersona())
                .fechaAlta(paciente.getFechaAlta())
                .fechaModificacion(paciente.getFechaModificacion())
                .referenciaBiometrica(reconocimiento.getReferenciaBiometrica())
                .similitud(searchResult.getSimilarity())
                .build();
    }

    private ReconocimientoAdminItemResponse toAdminItemResponse(Reconocimiento r) {
        Paciente p = r.getPaciente();

        String previewUrl = s3Service.generarUrlPreviewTemporal(
                r.getS3Bucket(),
                r.getS3Key()
        );

        return ReconocimientoAdminItemResponse.builder()
                .id(r.getId())
                .pacienteId(p != null ? p.getId() : null)
                .pacienteDni(p != null ? p.getDni() : null)
                .pacienteNombreCompleto(p != null
                        ? ((p.getApellido() == null ? "" : p.getApellido()) + ", " + (p.getNombre() == null ? "" : p.getNombre()))
                        : null)
                .faceId(r.getReferenciaBiometrica())
                .externalImageId(r.getExternalImageId())
                .s3Bucket(r.getS3Bucket())
                .s3Key(r.getS3Key())
                .collectionId(r.getCollectionId())
                .previewUrl(previewUrl)
                .build();
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
