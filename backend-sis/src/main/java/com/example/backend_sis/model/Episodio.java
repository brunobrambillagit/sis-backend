package com.example.backend_sis.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "episodio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK a paciente
    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // Usuario que admisiona (administrativo). Si todavía no lo usás, puede quedar nullable.
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio", nullable = false, length = 20)
    private TipoServicio tipoServicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_atencion", nullable = false, length = 20)
    private EstadoAtencion estadoAtencion;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_egreso")
    private LocalDateTime fechaEgreso;

    @PrePersist
    public void prePersist() {
        if (fechaIngreso == null) {
            fechaIngreso = LocalDateTime.now();
        }
        if (estadoAtencion == null) {
            estadoAtencion = EstadoAtencion.EN_ESPERA; // al crear episodio: EN_ESPERA
        }
    }
}