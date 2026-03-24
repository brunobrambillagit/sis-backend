package com.example.backend_sis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "turno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @OneToOne
    @JoinColumn(name = "episodio_id", unique = true)
    private Episodio episodio;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_desde", nullable = false)
    private LocalTime horaDesde;

    @Column(name = "hora_hasta", nullable = false)
    private LocalTime horaHasta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_turno", nullable = false, length = 20)
    private EstadoTurno estadoTurno;

    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_llegada")
    private LocalDateTime fechaLlegada;

    @Column(name = "observacion", length = 300)
    private String observacion;

    @PrePersist
    public void prePersist() {
        if (estadoTurno == null) {
            estadoTurno = EstadoTurno.DISPONIBLE;
        }
    }
}