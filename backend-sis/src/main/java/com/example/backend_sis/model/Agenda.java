package com.example.backend_sis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agenda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String especialidad;

    @Column(nullable = false)
    private Integer duracionTurnoMinutos;

    @Column(nullable = false)
    private Boolean activa;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AgendaDiaAtencion> diasAtencion = new ArrayList<>();

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AgendaPermisoMedico> permisosMedicos = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (activa == null) {
            activa = true;
        }
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}
