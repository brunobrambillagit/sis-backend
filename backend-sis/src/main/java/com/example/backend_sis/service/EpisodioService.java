package com.example.backend_sis.service;

import com.example.backend_sis.dto.EpisodioCreateRequest;
import com.example.backend_sis.dto.EpisodioListItemResponse;
import com.example.backend_sis.model.*;
import com.example.backend_sis.repository.EpisodioRepository;
import com.example.backend_sis.repository.PacienteRepository;
import com.example.backend_sis.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EpisodioService {

    private final EpisodioRepository episodioRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    private static final List<EstadoAtencion> ESTADOS_ACTIVOS = List.of(
            EstadoAtencion.EN_ESPERA,
            EstadoAtencion.EN_ATENCION,
            EstadoAtencion.FINALIZADO
    );

    public Episodio crearEpisodio(EpisodioCreateRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean yaTieneActivo = episodioRepository.existsByPaciente_IdAndEstadoAtencionIn(
                paciente.getId(),
                ESTADOS_ACTIVOS
        );

        if (yaTieneActivo) {
            throw new RuntimeException("El paciente ya tiene un episodio activo");
        }

        Episodio episodio = Episodio.builder()
                .paciente(paciente)
                .usuario(usuario)
                .tipoServicio(request.getTipoServicio())
                .estadoAtencion(EstadoAtencion.EN_ESPERA)
                .fechaIngreso(LocalDateTime.now())
                .fechaEgreso(null)
                .build();

        return episodioRepository.save(episodio);
    }

    public List<EpisodioListItemResponse> listarActivosPorServicio(TipoServicio tipoServicio) {
        return episodioRepository
                .findByTipoServicioAndEstadoAtencionInOrderByFechaIngresoDesc(tipoServicio, ESTADOS_ACTIVOS)
                .stream()
                .map(episodio -> EpisodioListItemResponse.builder()
                        .episodioId(episodio.getId())
                        .pacienteId(episodio.getPaciente().getId())
                        .dni(episodio.getPaciente().getDni())
                        .nombre(episodio.getPaciente().getNombre())
                        .apellido(episodio.getPaciente().getApellido())
                        .tipoServicio(episodio.getTipoServicio())
                        .estadoAtencion(episodio.getEstadoAtencion())
                        .fechaIngreso(episodio.getFechaIngreso())
                        .build())
                .toList();
    }

    public Episodio cambiarEstado(Long episodioId, EstadoAtencion nuevoEstado, Long usuarioId) {
        Episodio episodio = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new RuntimeException("Episodio no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        EstadoAtencion estadoActual = episodio.getEstadoAtencion();

        if (estadoActual == EstadoAtencion.ALTA) {
            throw new RuntimeException("No se puede modificar un episodio que ya está en ALTA");
        }

        validarCambioEstado(estadoActual, nuevoEstado, usuario);

        episodio.setEstadoAtencion(nuevoEstado);

        if (nuevoEstado == EstadoAtencion.ALTA) {
            episodio.setFechaEgreso(LocalDateTime.now());
        } else {
            episodio.setFechaEgreso(null);
        }

        return episodioRepository.save(episodio);
    }

    private void validarCambioEstado(EstadoAtencion estadoActual, EstadoAtencion nuevoEstado, Usuario usuario) {
        Usuario.Rol rol = usuario.getRol();

        if (nuevoEstado == EstadoAtencion.ALTA) {
            if (rol != Usuario.Rol.ADMINISTRATIVO) {
                throw new RuntimeException("Solo un ADMINISTRATIVO puede dar el alta");
            }

            if (estadoActual != EstadoAtencion.FINALIZADO) {
                throw new RuntimeException("El alta solo se puede realizar desde FINALIZADO");
            }

            return;
        }

        if (rol != Usuario.Rol.MEDICO) {
            throw new RuntimeException("Solo un MEDICO puede cambiar a este estado");
        }

        boolean transicionValida =
                (estadoActual == EstadoAtencion.EN_ESPERA && nuevoEstado == EstadoAtencion.EN_ATENCION)
                        || (estadoActual == EstadoAtencion.EN_ATENCION && nuevoEstado == EstadoAtencion.FINALIZADO)
                        || (estadoActual == EstadoAtencion.FINALIZADO && nuevoEstado == EstadoAtencion.EN_ATENCION);

        if (!transicionValida) {
            throw new RuntimeException("Transición no permitida: " + estadoActual + " -> " + nuevoEstado);
        }
    }
}