package com.example.backend_sis.service;

import com.example.backend_sis.dto.*;
import com.example.backend_sis.model.*;
import com.example.backend_sis.repository.EpisodioRepository;
import com.example.backend_sis.repository.EvolucionEpisodioRepository;
import com.example.backend_sis.repository.ObservacionEpisodioRepository;
import com.example.backend_sis.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EpisodioDetalleService {

    private final EpisodioRepository episodioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObservacionEpisodioRepository observacionRepository;
    private final EvolucionEpisodioRepository evolucionRepository;

    public EpisodioDetalleResponse obtenerDetalle(Long episodioId) {
        Episodio episodio = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new RuntimeException("Episodio no encontrado"));

        List<ObservacionEpisodioResponse> observaciones = observacionRepository
                .findByEpisodioIdOrderByFechaRegistroDesc(episodioId)
                .stream()
                .map(o -> ObservacionEpisodioResponse.builder()
                        .id(o.getId())
                        .observacion(o.getObservacion())
                        .fechaRegistro(o.getFechaRegistro())
                        .usuarioId(o.getUsuario().getId())
                        .nombreUsuario(o.getUsuario().getNombre() + " " + o.getUsuario().getApellido())
                        .build())
                .toList();

        List<EvolucionEpisodioResponse> evoluciones = evolucionRepository
                .findByEpisodioIdOrderByFechaRegistroDesc(episodioId)
                .stream()
                .map(e -> EvolucionEpisodioResponse.builder()
                        .id(e.getId())
                        .diagnosticos(e.getDiagnosticos())
                        .evolucion(e.getEvolucion())
                        .medicacionIndicaciones(e.getMedicacionIndicaciones())
                        .estudiosSolicitados(e.getEstudiosSolicitados())
                        .fechaRegistro(e.getFechaRegistro())
                        .usuarioId(e.getUsuario().getId())
                        .nombreUsuario(e.getUsuario().getNombre() + " " + e.getUsuario().getApellido())
                        .build())
                .toList();

        String observacionesHC = null;
        if (episodio.getPaciente().getHistoriaClinica() != null) {
            observacionesHC = episodio.getPaciente().getHistoriaClinica().getObservaciones();
        }

        return EpisodioDetalleResponse.builder()
                .episodioId(episodio.getId())
                .tipoServicio(episodio.getTipoServicio())
                .estadoAtencion(episodio.getEstadoAtencion())
                .fechaIngreso(episodio.getFechaIngreso())
                .fechaEgreso(episodio.getFechaEgreso())
                .pacienteId(episodio.getPaciente().getId())
                .dni(episodio.getPaciente().getDni())
                .nombre(episodio.getPaciente().getNombre())
                .apellido(episodio.getPaciente().getApellido())
                .nroHistoriaClinica(episodio.getPaciente().getNroHistoriaClinica())
                .observacionesHistoriaClinica(observacionesHC)
                .observaciones(observaciones)
                .evoluciones(evoluciones)
                .build();
    }

    public ObservacionEpisodioResponse agregarObservacion(Long episodioId, ObservacionEpisodioCreateRequest request) {
        Episodio episodio = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new RuntimeException("Episodio no encontrado"));

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ObservacionEpisodio observacion = ObservacionEpisodio.builder()
                .episodio(episodio)
                .usuario(usuario)
                .observacion(request.getObservacion())
                .build();

        ObservacionEpisodio guardada = observacionRepository.save(observacion);

        return ObservacionEpisodioResponse.builder()
                .id(guardada.getId())
                .observacion(guardada.getObservacion())
                .fechaRegistro(guardada.getFechaRegistro())
                .usuarioId(usuario.getId())
                .nombreUsuario(usuario.getNombre() + " " + usuario.getApellido())
                .build();
    }

    public EvolucionEpisodioResponse agregarEvolucion(Long episodioId, EvolucionEpisodioCreateRequest request) {
        Episodio episodio = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new RuntimeException("Episodio no encontrado"));

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        EvolucionEpisodio evolucion = EvolucionEpisodio.builder()
                .episodio(episodio)
                .usuario(usuario)
                .diagnosticos(request.getDiagnosticos())
                .evolucion(request.getEvolucion())
                .medicacionIndicaciones(request.getMedicacionIndicaciones())
                .estudiosSolicitados(request.getEstudiosSolicitados())
                .build();

        EvolucionEpisodio guardada = evolucionRepository.save(evolucion);

        return EvolucionEpisodioResponse.builder()
                .id(guardada.getId())
                .diagnosticos(guardada.getDiagnosticos())
                .evolucion(guardada.getEvolucion())
                .medicacionIndicaciones(guardada.getMedicacionIndicaciones())
                .estudiosSolicitados(guardada.getEstudiosSolicitados())
                .fechaRegistro(guardada.getFechaRegistro())
                .usuarioId(usuario.getId())
                .nombreUsuario(usuario.getNombre() + " " + usuario.getApellido())
                .build();
    }
}
