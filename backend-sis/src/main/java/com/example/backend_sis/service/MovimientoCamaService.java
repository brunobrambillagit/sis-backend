package com.example.backend_sis.service;

import com.example.backend_sis.dto.MovimientoCamaResponse;
import com.example.backend_sis.model.MovimientoCama;
import com.example.backend_sis.repository.MovimientoCamaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoCamaService {

    private final MovimientoCamaRepository movimientoCamaRepository;

    @Transactional(readOnly = true)
    public List<MovimientoCamaResponse> obtenerPorEpisodio(Long episodioId) {
        return movimientoCamaRepository.findByEpisodioIdOrderByFechaMovimientoAsc(episodioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MovimientoCamaResponse toResponse(MovimientoCama mov) {
        String nombreCompleto = "-";
        Long usuarioId = null;

        if (mov.getUsuario() != null) {
            usuarioId = mov.getUsuario().getId();
            nombreCompleto = (mov.getUsuario().getNombre() + " " + mov.getUsuario().getApellido()).trim();
        }

        return new MovimientoCamaResponse(
                mov.getId(),
                mov.getEpisodio().getId(),
                mov.getCamaOrigen() != null ? mov.getCamaOrigen().getId() : null,
                mov.getCamaOrigen() != null ? mov.getCamaOrigen().getCodigo() : null,
                mov.getCamaDestino() != null ? mov.getCamaDestino().getId() : null,
                mov.getCamaDestino() != null ? mov.getCamaDestino().getCodigo() : null,
                usuarioId,
                nombreCompleto,
                mov.getTipoMovimiento(),
                mov.getFechaMovimiento()
        );
    }
}