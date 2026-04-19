package com.example.backend_sis.service;

import com.example.backend_sis.dto.*;
import com.example.backend_sis.exception.BusinessException;
import com.example.backend_sis.model.Paciente;
import com.example.backend_sis.model.ReconocimientoHuella;
import com.example.backend_sis.repository.PacienteRepository;
import com.example.backend_sis.repository.ReconocimientoHuellaRepository;
import com.machinezoo.sourceafis.FingerprintTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Base64;


import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HuellaService {

    private static final String PROVEEDOR_SOURCE_AFIS_LOCAL = "SOURCE_AFIS_LOCAL";

    private final PacienteRepository pacienteRepository;
    private final ReconocimientoHuellaRepository reconocimientoHuellaRepository;
    private final SourceAfisService sourceAfisService;

    @Transactional
    public HuellaRegistroResponse registrarHuella(HuellaRegistroRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new BusinessException("No existe paciente con id " + request.getPacienteId()));

        boolean yaTieneHuellaActiva = reconocimientoHuellaRepository.existsByPacienteIdAndDedoAndActivoTrue(
                request.getPacienteId(),
                request.getDedo()
        );

        if (yaTieneHuellaActiva) {
            throw new BusinessException("El paciente ya tiene registrada una huella activa para el dedo " + request.getDedo() + ".");
        }

        byte[] rawImage = sourceAfisService.decodeRawBase64(request.getRawImageBase64());
        FingerprintTemplate template = sourceAfisService.buildTemplate(
                rawImage,
                request.getWidth(),
                request.getHeight(),
                request.getDpi()

        );

        Date ahora = new Date();

        ReconocimientoHuella entidad = ReconocimientoHuella.builder()
                .paciente(paciente)
                .dedo(request.getDedo())
                .templateBiometrico(sourceAfisService.serializeTemplate(template))
                .anchoImagen(request.getWidth())
                .altoImagen(request.getHeight())
                .dpi(request.getDpi())
                .calidadCaptura(request.getQuality())
                .proveedor(PROVEEDOR_SOURCE_AFIS_LOCAL)
                .activo(true)
                .fechaCaptura(ahora)
                .fechaVinculoPaciente(ahora)
                .imagenPreview(rawImage)
                .build();

        ReconocimientoHuella guardada = reconocimientoHuellaRepository.save(entidad);
        return toRegistroResponse(guardada);
    }

    @Transactional(readOnly = true)
    public PacienteHuellaMatchResponse buscarPacientePorHuella(HuellaBusquedaRequest request) {
        List<ReconocimientoHuella> huellasActivas = reconocimientoHuellaRepository.findByActivoTrueOrderByIdAsc();

        if (huellasActivas.isEmpty()) {
            throw new BusinessException("No hay huellas registradas en el sistema.");
        }

        byte[] rawImage = sourceAfisService.decodeRawBase64(request.getRawImageBase64());
        FingerprintTemplate probe = sourceAfisService.buildTemplate(
                rawImage,
                request.getWidth(),
                request.getHeight(),
                request.getDpi()
        );

        ReconocimientoHuella mejorHuella = null;
        double mejorScore = Double.NEGATIVE_INFINITY;

        for (ReconocimientoHuella candidata : huellasActivas) {
            FingerprintTemplate candidate = sourceAfisService.deserializeTemplate(candidata.getTemplateBiometrico());
            double score = sourceAfisService.match(probe, candidate);

            if (score > mejorScore) {
                mejorScore = score;
                mejorHuella = candidata;
            }
        }

        double threshold = sourceAfisService.getMatchThreshold();

        if (mejorHuella == null || mejorScore < threshold) {
            throw new BusinessException("No se encontró un paciente registrado con esa huella.");
        }

        Paciente paciente = mejorHuella.getPaciente();

        return PacienteHuellaMatchResponse.builder()
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
                .reconocimientoHuellaId(mejorHuella.getId())
                .dedo(mejorHuella.getDedo())
                .score(mejorScore)
                .threshold(threshold)
                .build();
    }

    @Transactional(readOnly = true)
    public List<HuellaPacienteItemResponse> listarHuellasPorPaciente(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new BusinessException("No existe paciente con id " + pacienteId);
        }

        return reconocimientoHuellaRepository.findByPacienteIdOrderByFechaCapturaDesc(pacienteId)
                .stream()
                .map(this::toPacienteItemResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HuellaAdminItemResponse> listarHuellasAdmin() {
        return reconocimientoHuellaRepository.findAll()
                .stream()
                .map(this::toAdminItemResponse)
                .toList();
    }

    @Transactional
    public void eliminarHuella(Long reconocimientoHuellaId) {
        ReconocimientoHuella huella = reconocimientoHuellaRepository.findById(reconocimientoHuellaId)
                .orElseThrow(() -> new BusinessException("No existe la huella con id " + reconocimientoHuellaId));

        if (!Boolean.TRUE.equals(huella.getActivo())) {
            throw new BusinessException("La huella ya se encuentra inactiva.");
        }

        huella.setActivo(false);
        reconocimientoHuellaRepository.save(huella);
    }

    private HuellaRegistroResponse toRegistroResponse(ReconocimientoHuella h) {
        Paciente paciente = h.getPaciente();
        String nombreCompleto = (paciente.getApellido() == null ? "" : paciente.getApellido())
                + ", "
                + (paciente.getNombre() == null ? "" : paciente.getNombre());

        return HuellaRegistroResponse.builder()
                .id(h.getId())
                .pacienteId(paciente.getId())
                .pacienteDni(paciente.getDni())
                .pacienteNombreCompleto(nombreCompleto)
                .dedo(h.getDedo())
                .width(h.getAnchoImagen())
                .height(h.getAltoImagen())
                .dpi(h.getDpi())
                .quality(h.getCalidadCaptura())
                .proveedor(h.getProveedor())
                .activo(h.getActivo())
                .fechaCaptura(h.getFechaCaptura())
                .build();
    }

    private HuellaPacienteItemResponse toPacienteItemResponse(ReconocimientoHuella h) {
        return HuellaPacienteItemResponse.builder()
                .id(h.getId())
                .dedo(h.getDedo())
                .width(h.getAnchoImagen())
                .height(h.getAltoImagen())
                .dpi(h.getDpi())
                .quality(h.getCalidadCaptura())
                .proveedor(h.getProveedor())
                .activo(h.getActivo())
                .fechaCaptura(h.getFechaCaptura())
                .build();
    }

    private HuellaAdminItemResponse toAdminItemResponse(ReconocimientoHuella h) {
        Paciente paciente = h.getPaciente();

        String imagenPreviewBase64 = h.getImagenPreview() != null
                ? Base64.getEncoder().encodeToString(h.getImagenPreview())
                : null;

        return HuellaAdminItemResponse.builder()
                .id(h.getId())
                .pacienteId(paciente != null ? paciente.getId() : null)
                .pacienteDni(paciente != null ? paciente.getDni() : null)
                .pacienteNombreCompleto(
                        paciente != null
                                ? ((paciente.getApellido() == null ? "" : paciente.getApellido()) + ", "
                                + (paciente.getNombre() == null ? "" : paciente.getNombre()))
                                : null
                )
                .dedo(h.getDedo())
                .width(h.getAnchoImagen())
                .height(h.getAltoImagen())
                .dpi(h.getDpi())
                .quality(h.getCalidadCaptura())
                .proveedor(h.getProveedor())
                .activo(h.getActivo())
                .fechaCaptura(h.getFechaCaptura())
                .imagenPreviewBase64(imagenPreviewBase64)
                .build();
    }
}
