package com.example.backend_sis.service;

import com.example.backend_sis.dto.*;
import com.example.backend_sis.model.Agenda;
import com.example.backend_sis.model.AgendaDiaAtencion;
import com.example.backend_sis.model.AgendaPermisoMedico;
import com.example.backend_sis.model.Turno;
import com.example.backend_sis.model.Usuario;
import com.example.backend_sis.repository.AgendaRepository;
import com.example.backend_sis.repository.TurnoRepository;
import com.example.backend_sis.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurnoRepository turnoRepository;

    @Transactional
    public AgendaResponse crearAgenda(AgendaCreateRequest request) {
        validarAgenda(request);

        Agenda agenda = Agenda.builder()
                .nombre(request.getNombre().trim())
                .especialidad(request.getEspecialidad().trim())
                .duracionTurnoMinutos(request.getDuracionTurnoMinutos())
                .activa(true)
                .build();

        for (AgendaDiaAtencionRequest diaRequest : request.getDiasAtencion()) {
            AgendaDiaAtencion dia = AgendaDiaAtencion.builder()
                    .agenda(agenda)
                    .diaSemana(diaRequest.getDiaSemana())
                    .horaInicio(diaRequest.getHoraInicio())
                    .horaFin(diaRequest.getHoraFin())
                    .build();
            agenda.getDiasAtencion().add(dia);
        }

        if (request.getMedicoIdsConPermiso() != null) {
            Set<Long> idsUnicos = new LinkedHashSet<>(request.getMedicoIdsConPermiso());
            for (Long medicoId : idsUnicos) {
                Usuario medico = usuarioRepository.findById(medicoId)
                        .orElseThrow(() -> new RuntimeException("Médico no encontrado: " + medicoId));

                if (medico.getRol() != Usuario.Rol.MEDICO) {
                    throw new RuntimeException("El usuario " + medicoId + " no tiene rol MEDICO");
                }

                AgendaPermisoMedico permiso = AgendaPermisoMedico.builder()
                        .agenda(agenda)
                        .usuario(medico)
                        .activo(true)
                        .build();
                agenda.getPermisosMedicos().add(permiso);
            }
        }

        Agenda agendaGuardada = agendaRepository.save(agenda);
        return mapToResponse(agendaGuardada);
    }

    @Transactional
    public List<TurnoListItemResponse> generarTurnos(Long agendaId, AgendaGenerarTurnosRequest request) {
        if (request.getFechaDesde() == null || request.getFechaHasta() == null) {
            throw new RuntimeException("Debe informar fechaDesde y fechaHasta");
        }
        if (request.getFechaHasta().isBefore(request.getFechaDesde())) {
            throw new RuntimeException("La fechaHasta no puede ser menor a la fechaDesde");
        }

        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new RuntimeException("Agenda no encontrada"));

        if (Boolean.FALSE.equals(agenda.getActiva())) {
            throw new RuntimeException("La agenda está inactiva");
        }

        List<Turno> nuevosTurnos = new ArrayList<>();

        for (LocalDate fecha = request.getFechaDesde(); !fecha.isAfter(request.getFechaHasta()); fecha = fecha.plusDays(1)) {
            for (AgendaDiaAtencion dia : agenda.getDiasAtencion()) {
                if (dia.getDiaSemana() != fecha.getDayOfWeek()) {
                    continue;
                }

                LocalTime horaActual = dia.getHoraInicio();
                while (!horaActual.plusMinutes(agenda.getDuracionTurnoMinutos()).isAfter(dia.getHoraFin())) {
                    LocalTime horaFinBloque = horaActual.plusMinutes(agenda.getDuracionTurnoMinutos());

                    boolean existe = turnoRepository.existsByAgenda_IdAndFechaAndHoraDesde(
                            agenda.getId(),
                            fecha,
                            horaActual
                    );

                    if (!existe) {
                        Turno turno = Turno.builder()
                                .agenda(agenda)
                                .fecha(fecha)
                                .horaDesde(horaActual)
                                .horaHasta(horaFinBloque)
                                .build();
                        nuevosTurnos.add(turno);
                    }

                    horaActual = horaFinBloque;
                }
            }
        }

        return turnoRepository.saveAll(nuevosTurnos)
                .stream()
                .map(this::mapTurno)
                .toList();
    }

    public List<AgendaResponse> listarAgendas() {
        return agendaRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AgendaResponse> listarAgendasDeMedico(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() != Usuario.Rol.MEDICO) {
            throw new RuntimeException("Solo un MEDICO puede consultar sus agendas");
        }

        return agendaRepository.findAll().stream()
                .filter(agenda -> agenda.getPermisosMedicos().stream()
                        .anyMatch(p -> Boolean.TRUE.equals(p.getActivo()) && p.getUsuario().getId().equals(usuarioId)))
                .map(this::mapToResponse)
                .toList();
    }

    private void validarAgenda(AgendaCreateRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la agenda es obligatorio");
        }
        if (request.getEspecialidad() == null || request.getEspecialidad().isBlank()) {
            throw new RuntimeException("La especialidad es obligatoria");
        }
        if (request.getDuracionTurnoMinutos() == null || request.getDuracionTurnoMinutos() <= 0) {
            throw new RuntimeException("La duración del turno debe ser mayor a 0");
        }
        if (request.getDiasAtencion() == null || request.getDiasAtencion().isEmpty()) {
            throw new RuntimeException("Debe informar al menos un día de atención");
        }

        Set<java.time.DayOfWeek> dias = new HashSet<>();
        for (AgendaDiaAtencionRequest dia : request.getDiasAtencion()) {
            if (dia.getDiaSemana() == null) {
                throw new RuntimeException("Debe informar el día de semana");
            }
            if (dia.getHoraInicio() == null || dia.getHoraFin() == null) {
                throw new RuntimeException("Debe informar horaInicio y horaFin");
            }
            if (!dia.getHoraInicio().isBefore(dia.getHoraFin())) {
                throw new RuntimeException("La horaInicio debe ser menor a la horaFin");
            }
            if (!dias.add(dia.getDiaSemana())) {
                throw new RuntimeException("No puede repetir el día " + dia.getDiaSemana());
            }

            long minutosTotales = java.time.Duration.between(dia.getHoraInicio(), dia.getHoraFin()).toMinutes();
            if (minutosTotales < request.getDuracionTurnoMinutos()) {
                throw new RuntimeException("La franja horaria no alcanza para un turno en el día " + dia.getDiaSemana());
            }
            if (minutosTotales % request.getDuracionTurnoMinutos() != 0) {
                throw new RuntimeException("La franja horaria del día " + dia.getDiaSemana() + " debe ser múltiplo exacto de la duración del turno");
            }
        }
    }

    private AgendaResponse mapToResponse(Agenda agenda) {
        return AgendaResponse.builder()
                .id(agenda.getId())
                .nombre(agenda.getNombre())
                .especialidad(agenda.getEspecialidad())
                .duracionTurnoMinutos(agenda.getDuracionTurnoMinutos())
                .activa(agenda.getActiva())
                .fechaCreacion(agenda.getFechaCreacion())
                .diasAtencion(agenda.getDiasAtencion().stream()
                        .map(d -> AgendaDiaAtencionResponse.builder()
                                .diaSemana(d.getDiaSemana())
                                .horaInicio(d.getHoraInicio())
                                .horaFin(d.getHoraFin())
                                .build())
                        .toList())
                .medicosConPermiso(agenda.getPermisosMedicos().stream()
                        .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                        .map(p -> MedicoAgendaResponse.builder()
                                .usuarioId(p.getUsuario().getId())
                                .nombreCompleto(p.getUsuario().getNombre() + " " + p.getUsuario().getApellido())
                                .email(p.getUsuario().getEmail())
                                .build())
                        .toList())
                .build();
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
}
