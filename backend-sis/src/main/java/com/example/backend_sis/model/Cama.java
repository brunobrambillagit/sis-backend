package com.example.backend_sis.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cama")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cama {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCama estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio", nullable = false, length = 30)
    private TipoServicio tipoServicio;
}
