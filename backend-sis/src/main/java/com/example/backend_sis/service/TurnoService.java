package com.example.backend_sis.service;

import com.example.backend_sis.dto.*;
import com.example.backend_sis.model.*;
import com.example.backend_sis.repository.AgendaPermisoMedicoRepository;
import com.example.backend_sis.repository.PacienteRepository;
import com.example.backend_sis.repository.TurnoRepository;
import com.example.backend_sis.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private static final Set<EstadoTurno> ESTADOS_VISIBLES_ADMIN = Set.of(
            EstadoTurno.DISPONIBLE,
            EstadoTurno.CITADO,
            EstadoTurno.EN_ESPERA,
            EstadoTurno.EN_ATENCION,
            EstadoTurno.FINALIZADO,
            EstadoTurno.ALTA,
            EstadoTurno.AUSENTE,
            EstadoTurno.CANCELADO,
            EstadoTurno.REPROGRAMADO
    );

    private static final Set<EstadoTurno> ESTADOS_VISIBLES_MEDICO = Set.of(
            EstadoTurno.EN_ESPERA,
            EstadoTurno.EN_ATENCION,
            EstadoTurno.FINALIZADO
    );

    private final TurnoRepository turnoRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AgendaPermisoMedicoRepository agendaPermisoMedicoRepository;
    private final EpisodioService episodioService;

    @Transactional
    public TurnoComprobanteResponse asignarPaciente(Long turnoId, TurnoAsignarPacienteRequest request) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (turno.getEstadoTurno() != EstadoTurno.DISPONIBLE) {
            throw new RuntimeException("Solo se puede asignar un paciente a un turno DISPONIBLE");
        }

        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        turno.setPaciente(paciente);
        turno.setEstadoTurno(EstadoTurno.CITADO);
        turno.setFechaReserva(LocalDateTime.now());
        turno.setObservacion(request.getObservacion());

        Turno guardado = turnoRepository.save(turno);
        return mapComprobante(guardado);
    }


    @Transactional
    public TurnoListItemResponse cambiarEstado(Long turnoId, EstadoTurno nuevoEstado, Long usuarioId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        validarCambioEstado(turno, nuevoEstado, usuario);

        if (nuevoEstado == EstadoTurno.EN_ESPERA) {
            Episodio episodio = episodioService.crearEpisodioConsultoriosDesdeTurno(
                    turno.getPaciente().getId(),
                    usuarioId
            );
            turno.setEpisodio(episodio);
            turno.setFechaLlegada(LocalDateTime.now());
            turno.setEstadoTurno(EstadoTurno.EN_ESPERA);
            return mapTurno(turnoRepository.save(turno));
        }

        if (nuevoEstado == EstadoTurno.EN_ATENCION) {
            episodioService.cambiarEstado(turno.getEpisodio().getId(), EstadoAtencion.EN_ATENCION, usuarioId);
            turno.setEstadoTurno(EstadoTurno.EN_ATENCION);
            return mapTurno(turnoRepository.save(turno));
        }

        if (nuevoEstado == EstadoTurno.FINALIZADO) {
            episodioService.cambiarEstado(turno.getEpisodio().getId(), EstadoAtencion.FINALIZADO, usuarioId);
            turno.setEstadoTurno(EstadoTurno.FINALIZADO);
            return mapTurno(turnoRepository.save(turno));
        }

        if (nuevoEstado == EstadoTurno.ALTA) {
            episodioService.cambiarEstado(turno.getEpisodio().getId(), EstadoAtencion.ALTA, usuarioId);
            turno.setEstadoTurno(EstadoTurno.ALTA);
            return mapTurno(turnoRepository.save(turno));
        }

        turno.setEstadoTurno(nuevoEstado);
        return mapTurno(turnoRepository.save(turno));
    }

    @Transactional(readOnly = true)
    public TurnoComprobanteResponse obtenerComprobante(Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (turno.getPaciente() == null) {
            throw new RuntimeException("El turno no tiene un paciente asignado");
        }

        return mapComprobante(turno);
    }

    @Transactional
    public TurnoListItemResponse reprogramarTurno(Long turnoOrigenId, TurnoReprogramarRequest request) {
        Turno turnoOrigen = turnoRepository.findById(turnoOrigenId)
                .orElseThrow(() -> new RuntimeException("Turno origen no encontrado"));

        Turno turnoDestino = turnoRepository.findById(request.getNuevoTurnoId())
                .orElseThrow(() -> new RuntimeException("Turno destino no encontrado"));

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() != Usuario.Rol.ADMINISTRATIVO) {
            throw new RuntimeException("Solo un ADMINISTRATIVO puede reprogramar turnos");
        }

        if (turnoOrigen.getEstadoTurno() != EstadoTurno.CITADO) {
            throw new RuntimeException("Solo se pueden reprogramar turnos CITADOS");
        }

        if (turnoDestino.getEstadoTurno() != EstadoTurno.DISPONIBLE) {
            throw new RuntimeException("El turno destino debe estar DISPONIBLE");
        }

        if (turnoOrigen.getPaciente() == null) {
            throw new RuntimeException("El turno origen no tiene un paciente asignado");
        }

        Paciente paciente = turnoOrigen.getPaciente();
        String observacion = turnoOrigen.getObservacion();

        turnoOrigen.setEstadoTurno(EstadoTurno.REPROGRAMADO);
        turnoOrigen.setPaciente(null);
        turnoOrigen.setFechaReserva(null);
        turnoOrigen.setObservacion("Reprogramado desde este bloque");
        turnoRepository.save(turnoOrigen);

        Turno reemplazoLibre = Turno.builder()
                .agenda(turnoOrigen.getAgenda())
                .fecha(turnoOrigen.getFecha())
                .horaDesde(turnoOrigen.getHoraDesde())
                .horaHasta(turnoOrigen.getHoraHasta())
                .estadoTurno(EstadoTurno.DISPONIBLE)
                .build();
        turnoRepository.save(reemplazoLibre);

        turnoDestino.setPaciente(paciente);
        turnoDestino.setEstadoTurno(EstadoTurno.CITADO);
        turnoDestino.setFechaReserva(LocalDateTime.now());
        turnoDestino.setObservacion(observacion);

        return mapTurno(turnoRepository.save(turnoDestino));
    }

    public List<TurnoListItemResponse> listarTurnosDelDiaAdmin(LocalDate fecha) {
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();

        return turnoRepository.findByFechaAndEstadoTurnoInOrderByHoraDesdeAsc(fechaConsulta, ESTADOS_VISIBLES_ADMIN)
                .stream()
                .map(this::mapTurno)
                .toList();
    }

    public List<TurnoListItemResponse> listarTurnosFiltradosAdmin(Long agendaId, LocalDate fecha, EstadoTurno estado) {
        if (agendaId == null) {
            throw new RuntimeException("Debés informar la agenda");
        }

        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        EstadoTurno estadoConsulta = estado != null ? estado : EstadoTurno.DISPONIBLE;

        return turnoRepository.findByAgenda_IdAndFechaAndEstadoTurnoOrderByHoraDesdeAsc(
                        agendaId,
                        fechaConsulta,
                        estadoConsulta
                )
                .stream()
                .map(this::mapTurno)
                .toList();
    }

    public List<TurnoListItemResponse> listarTurnosDelDiaMedico(Long usuarioId, LocalDate fecha) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() != Usuario.Rol.MEDICO) {
            throw new RuntimeException("Solo un MEDICO puede consultar esta vista");
        }

        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();

        return turnoRepository.findTurnosVisiblesPorMedicoYFecha(usuarioId, fechaConsulta)
                .stream()
                .filter(turno -> ESTADOS_VISIBLES_MEDICO.contains(turno.getEstadoTurno()))
                .map(this::mapTurno)
                .toList();
    }

    private void validarCambioEstado(Turno turno, EstadoTurno nuevoEstado, Usuario usuario) {
        EstadoTurno actual = turno.getEstadoTurno();

        if (nuevoEstado == EstadoTurno.EN_ESPERA) {
            if (usuario.getRol() != Usuario.Rol.ADMINISTRATIVO) {
                throw new RuntimeException("Solo un ADMINISTRATIVO puede registrar la llegada del paciente");
            }
            if (actual != EstadoTurno.CITADO) {
                throw new RuntimeException("Solo se puede pasar a EN_ESPERA desde CITADO");
            }
            if (turno.getPaciente() == null) {
                throw new RuntimeException("El turno no tiene paciente asignado");
            }
            return;
        }

        if (nuevoEstado == EstadoTurno.AUSENTE || nuevoEstado == EstadoTurno.CANCELADO) {
            if (usuario.getRol() != Usuario.Rol.ADMINISTRATIVO) {
                throw new RuntimeException("Solo un ADMINISTRATIVO puede cambiar a este estado");
            }
            if (actual != EstadoTurno.CITADO) {
                throw new RuntimeException("Solo se puede cambiar a " + nuevoEstado + " desde CITADO");
            }
            return;
        }

        if (nuevoEstado == EstadoTurno.EN_ATENCION || nuevoEstado == EstadoTurno.FINALIZADO) {
            if (usuario.getRol() != Usuario.Rol.MEDICO) {
                throw new RuntimeException("Solo un MEDICO puede cambiar a este estado");
            }
            validarPermisoAgenda(turno.getAgenda().getId(), usuario.getId());

            boolean transicionValida =
                    (actual == EstadoTurno.EN_ESPERA && nuevoEstado == EstadoTurno.EN_ATENCION)
                            || (actual == EstadoTurno.EN_ATENCION && nuevoEstado == EstadoTurno.FINALIZADO)
                            || (actual == EstadoTurno.FINALIZADO && nuevoEstado == EstadoTurno.EN_ATENCION);

            if (!transicionValida) {
                throw new RuntimeException("Cambio de estado no permitido");
            }
            return;
        }

        if (nuevoEstado == EstadoTurno.ALTA) {
            if (usuario.getRol() != Usuario.Rol.ADMINISTRATIVO) {
                throw new RuntimeException("Solo un ADMINISTRATIVO puede cerrar la cita");
            }
            if (actual != EstadoTurno.FINALIZADO) {
                throw new RuntimeException("La cita finalizada solo se puede realizar desde FINALIZADO");
            }
            return;
        }

        throw new RuntimeException("Cambio de estado no permitido");
    }

    private void validarPermisoAgenda(Long agendaId, Long usuarioId) {
        boolean tienePermiso = agendaPermisoMedicoRepository.existsByAgenda_IdAndUsuario_IdAndActivoTrue(agendaId, usuarioId);
        if (!tienePermiso) {
            throw new RuntimeException("El médico no tiene permisos sobre esta agenda");
        }
    }

    private TurnoListItemResponse mapTurno(Turno turno) {
        return TurnoListItemResponse.builder()
                .turnoId(turno.getId())
                .agendaId(turno.getAgenda().getId())
                .agendaNombre(turno.getAgenda().getNombre())
                .especialidad(turno.getAgenda().getEspecialidad())
                .fecha(turno.getFecha())
                .horaDesde(turno.getHoraDesde())
                .horaHasta(turno.getHoraHasta())
                .estadoTurno(turno.getEstadoTurno())
                .pacienteId(turno.getPaciente() != null ? turno.getPaciente().getId() : null)
                .pacienteDni(turno.getPaciente() != null ? turno.getPaciente().getDni() : null)
                .pacienteNombre(turno.getPaciente() != null ? turno.getPaciente().getNombre() : null)
                .pacienteApellido(turno.getPaciente() != null ? turno.getPaciente().getApellido() : null)
                .episodioId(turno.getEpisodio() != null ? turno.getEpisodio().getId() : null)
                .fechaReserva(turno.getFechaReserva())
                .fechaLlegada(turno.getFechaLlegada())
                .observacion(turno.getObservacion())
                .build();
    }

    private TurnoComprobanteResponse mapComprobante(Turno turno) {
        List<String> medicosAgenda = turno.getAgenda().getPermisosMedicos()
                .stream()
                .filter(permiso -> Boolean.TRUE.equals(permiso.getActivo()))
                .map(permiso -> {
                    Usuario medico = permiso.getUsuario();
                    String nombre = medico.getNombre() != null ? medico.getNombre() : "";
                    String apellido = medico.getApellido() != null ? medico.getApellido() : "";
                    return (apellido + ", " + nombre).trim().replaceAll("^,\\s*", "");
                })
                .toList();

        return TurnoComprobanteResponse.builder()
                .turnoId(turno.getId())
                .agendaNombre(turno.getAgenda().getNombre())
                .especialidad(turno.getAgenda().getEspecialidad())
                .fecha(turno.getFecha())
                .horaDesde(turno.getHoraDesde())
                .horaHasta(turno.getHoraHasta())
                .pacienteDni(turno.getPaciente().getDni())
                .pacienteNombreCompleto(turno.getPaciente().getApellido() + ", " + turno.getPaciente().getNombre())
                .observacion(turno.getObservacion())
                .medicosAgenda(medicosAgenda)
                .build();
    }
}
