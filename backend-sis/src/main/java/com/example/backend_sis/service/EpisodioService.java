package com.example.backend_sis.service;

import com.example.backend_sis.dto.EpisodioCambioCamaRequest;
import com.example.backend_sis.dto.EpisodioCreateRequest;
import com.example.backend_sis.dto.EpisodioListItemResponse;
import com.example.backend_sis.model.*;
import com.example.backend_sis.repository.CamaRepository;
import com.example.backend_sis.repository.EpisodioRepository;
import com.example.backend_sis.repository.MovimientoCamaRepository;
import com.example.backend_sis.repository.PacienteRepository;
import com.example.backend_sis.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EpisodioService {

    private final EpisodioRepository episodioRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CamaRepository camaRepository;
    private final MovimientoCamaRepository movimientoCamaRepository;

    private static final List<EstadoAtencion> ESTADOS_ACTIVOS = List.of(
            EstadoAtencion.EN_ESPERA,
            EstadoAtencion.EN_ATENCION,
            EstadoAtencion.FINALIZADO
    );

    @Transactional
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

        Cama cama = null;
        if (request.getTipoServicio() == TipoServicio.HOSPITALIZACION) {
            if (request.getCamaId() == null) {
                throw new RuntimeException("Debe seleccionar una cama para hospitalización");
            }

            cama = camaRepository.findById(request.getCamaId())
                    .orElseThrow(() -> new RuntimeException("Cama no encontrada"));

            if (cama.getTipoServicio() != TipoServicio.HOSPITALIZACION) {
                throw new RuntimeException("La cama seleccionada no pertenece a hospitalización");
            }

            if (cama.getEstado() != EstadoCama.DISPONIBLE) {
                throw new RuntimeException("La cama seleccionada no está disponible");
            }

            cama.setEstado(EstadoCama.OCUPADA);
            camaRepository.save(cama);
        }

        Episodio episodio = Episodio.builder()
                .paciente(paciente)
                .usuario(usuario)
                .tipoServicio(request.getTipoServicio())
                .estadoAtencion(EstadoAtencion.EN_ESPERA)
                .fechaIngreso(LocalDateTime.now())
                .fechaEgreso(null)
                .cama(cama)
                .build();

        Episodio episodioGuardado = episodioRepository.save(episodio);

        if (episodioGuardado.getTipoServicio() == TipoServicio.HOSPITALIZACION && episodioGuardado.getCama() != null) {
            registrarMovimiento(
                    episodioGuardado,
                    null,
                    episodioGuardado.getCama(),
                    usuario,
                    TipoMovimientoCama.INGRESO
            );
        }

        return episodioGuardado;
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
                        .camaId(episodio.getCama() != null ? episodio.getCama().getId() : null)
                        .camaCodigo(episodio.getCama() != null ? episodio.getCama().getCodigo() : null)
                        .build())
                .toList();
    }

    @Transactional
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

            if (episodio.getTipoServicio() == TipoServicio.HOSPITALIZACION && episodio.getCama() != null) {
                Cama camaActual = episodio.getCama();

                registrarMovimiento(
                        episodio,
                        camaActual,
                        null,
                        usuario,
                        TipoMovimientoCama.ALTA
                );

                camaActual.setEstado(EstadoCama.DISPONIBLE);
                camaRepository.save(camaActual);
                episodio.setCama(null);
            }
        } else {
            episodio.setFechaEgreso(null);
        }

        return episodioRepository.save(episodio);
    }

    @Transactional
    public Episodio cambiarCama(Long episodioId, EpisodioCambioCamaRequest request) {
        if (request.getNuevaCamaId() == null) {
            throw new RuntimeException("Debe seleccionar una nueva cama");
        }

        Episodio episodio = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new RuntimeException("Episodio no encontrado"));

        if (episodio.getTipoServicio() != TipoServicio.HOSPITALIZACION) {
            throw new RuntimeException("Solo se puede cambiar cama en episodios de hospitalización");
        }

        if (!ESTADOS_ACTIVOS.contains(episodio.getEstadoAtencion())) {
            throw new RuntimeException("Solo se puede cambiar cama en episodios activos");
        }

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() != Usuario.Rol.ADMINISTRATIVO && usuario.getRol() != Usuario.Rol.MEDICO) {
            throw new RuntimeException("El usuario no tiene permisos para cambiar la cama");
        }

        Cama nuevaCama = camaRepository.findById(request.getNuevaCamaId())
                .orElseThrow(() -> new RuntimeException("La nueva cama no existe"));

        if (nuevaCama.getTipoServicio() != TipoServicio.HOSPITALIZACION) {
            throw new RuntimeException("La cama seleccionada no pertenece a hospitalización");
        }

        if (nuevaCama.getEstado() != EstadoCama.DISPONIBLE) {
            throw new RuntimeException("La cama seleccionada no está disponible");
        }

        Cama camaAnterior = episodio.getCama();

        if (camaAnterior != null && camaAnterior.getId().equals(nuevaCama.getId())) {
            throw new RuntimeException("El paciente ya se encuentra en esa cama");
        }

        nuevaCama.setEstado(EstadoCama.OCUPADA);
        camaRepository.save(nuevaCama);

        if (camaAnterior != null) {
            camaAnterior.setEstado(EstadoCama.DISPONIBLE);
            camaRepository.save(camaAnterior);
        }

        episodio.setCama(nuevaCama);
        Episodio episodioActualizado = episodioRepository.save(episodio);

        registrarMovimiento(
                episodioActualizado,
                camaAnterior,
                nuevaCama,
                usuario,
                TipoMovimientoCama.TRASLADO
        );

        return episodioActualizado;
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
            throw new RuntimeException("Cambio de estado no permitido");
        }
    }

    private void registrarMovimiento(Episodio episodio,
                                     Cama camaOrigen,
                                     Cama camaDestino,
                                     Usuario usuario,
                                     TipoMovimientoCama tipoMovimiento) {
        MovimientoCama movimiento = new MovimientoCama();
        movimiento.setEpisodio(episodio);
        movimiento.setCamaOrigen(camaOrigen);
        movimiento.setCamaDestino(camaDestino);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimientoCamaRepository.save(movimiento);
    }
}