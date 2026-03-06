package com.example.backend_sis.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(
        name = "paciente",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_paciente_dni", columnNames = "dni"),
                @UniqueConstraint(name = "uk_paciente_nro_hc", columnNames = "nroHistoriaClinica")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -----------------------------
    // Datos básicos
    // -----------------------------

    @Column(unique = true, nullable = false)
    private String dni;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    private Integer edad;
    private String sexo;

    @Column(unique = true)
    private String nroHistoriaClinica;

    @Enumerated(EnumType.STRING)
    private EstadoPersona estadoPersona;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    // -----------------------------
    // Relaciones
    // -----------------------------

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<Reconocimiento> reconocimientos;

    // NUEVA relación 1 ↔ 1
    @OneToOne(mappedBy = "paciente",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private HistoriaClinica historiaClinica;

    // -----------------------------
    // Enumeraciones
    // -----------------------------

    public enum EstadoPersona {
        VIVO, FALLECIDO
    }
}