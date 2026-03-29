package com.example.backend_sis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(unique = true, nullable = false)
    private String dni;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private LocalDate fechaNacimiento;

    private Integer edad;

    @Enumerated(EnumType.STRING)
    private tipoSexo sexo;

    @Enumerated(EnumType.STRING)
    private EstadoPersona estadoPersona;

    @Column(unique = true)
    private String nroHistoriaClinica;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<Reconocimiento> reconocimientos;

    @OneToOne(mappedBy = "paciente",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private HistoriaClinica historiaClinica;

    public enum EstadoPersona {
        VIVO, FALLECIDO
    }

    public enum tipoSexo {
        MASCULINO, FEMENINO
    }
}
