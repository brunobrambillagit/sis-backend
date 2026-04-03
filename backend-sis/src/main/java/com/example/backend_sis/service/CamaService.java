package com.example.backend_sis.service;

import com.example.backend_sis.dto.CamaAdminRequest;
import com.example.backend_sis.dto.CamaAdminResponse;
import com.example.backend_sis.dto.CamaResponse;
import com.example.backend_sis.model.Cama;
import com.example.backend_sis.model.EstadoCama;
import com.example.backend_sis.model.TipoServicio;
import com.example.backend_sis.repository.CamaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CamaService {

    private final CamaRepository camaRepository;

    public List<CamaResponse> listarDisponiblesHospitalizacion() {
        return camaRepository
                .findByTipoServicioAndEstadoOrderByCodigoAsc(TipoServicio.HOSPITALIZACION, EstadoCama.DISPONIBLE)
                .stream()
                .map(cama -> CamaResponse.builder()
                        .id(cama.getId())
                        .codigo(cama.getCodigo())
                        .descripcion(cama.getDescripcion())
                        .estado(cama.getEstado().name())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CamaAdminResponse> listarTodas() {
        return camaRepository.findAllByOrderByCodigoAsc()
                .stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional
    public CamaAdminResponse crear(CamaAdminRequest request) {
        validarRequest(request);

        if (camaRepository.existsByCodigoIgnoreCase(request.getCodigo().trim())) {
            throw new RuntimeException("Ya existe una cama con ese código.");
        }

        Cama cama = Cama.builder()
                .codigo(request.getCodigo().trim())
                .descripcion(request.getDescripcion().trim())
                .tipoServicio(TipoServicio.valueOf(request.getTipoServicio().toUpperCase()))
                .estado(EstadoCama.DISPONIBLE)
                .build();

        return toAdminResponse(camaRepository.save(cama));
    }

    @Transactional
    public CamaAdminResponse actualizar(Long camaId, CamaAdminRequest request) {
        validarRequest(request);

        Cama cama = camaRepository.findById(camaId)
                .orElseThrow(() -> new RuntimeException("Cama no encontrada."));

        if (camaRepository.existsByCodigoIgnoreCaseAndIdNot(request.getCodigo().trim(), camaId)) {
            throw new RuntimeException("Ya existe otra cama con ese código.");
        }

        cama.setCodigo(request.getCodigo().trim());
        cama.setDescripcion(request.getDescripcion().trim());
        cama.setTipoServicio(TipoServicio.valueOf(request.getTipoServicio().toUpperCase()));

        return toAdminResponse(camaRepository.save(cama));
    }

    private void validarRequest(CamaAdminRequest request) {
        if (request.getCodigo() == null || request.getCodigo().isBlank()) {
            throw new RuntimeException("El código es obligatorio.");
        }
        if (request.getDescripcion() == null || request.getDescripcion().isBlank()) {
            throw new RuntimeException("La descripción es obligatoria.");
        }
        if (request.getTipoServicio() == null || request.getTipoServicio().isBlank()) {
            throw new RuntimeException("El tipo de servicio es obligatorio.");
        }

        try {
            TipoServicio.valueOf(request.getTipoServicio().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Tipo de servicio inválido.");
        }
    }

    private CamaAdminResponse toAdminResponse(Cama cama) {
        return CamaAdminResponse.builder()
                .id(cama.getId())
                .codigo(cama.getCodigo())
                .descripcion(cama.getDescripcion())
                .tipoServicio(cama.getTipoServicio() != null ? cama.getTipoServicio().name() : null)
                .estado(cama.getEstado() != null ? cama.getEstado().name() : null)
                .build();
    }
}
