package com.example.backend_sis.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "agenda_permiso_medico",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_agenda_permiso_medico", columnNames = {"agenda_id", "usuario_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaPermisoMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Boolean activo;

    @PrePersist
    public void prePersist() {
        if (activo == null) {
            activo = true;
        }
    }
}
