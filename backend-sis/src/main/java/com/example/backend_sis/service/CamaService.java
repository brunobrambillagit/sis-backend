package com.example.backend_sis.service;

import com.example.backend_sis.dto.CamaResponse;
import com.example.backend_sis.model.EstadoCama;
import com.example.backend_sis.model.TipoServicio;
import com.example.backend_sis.repository.CamaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}