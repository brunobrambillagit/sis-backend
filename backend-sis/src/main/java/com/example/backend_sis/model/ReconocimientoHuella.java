package com.example.backend_sis.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(
        name = "reconocimiento_huella",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_huella_paciente_dedo_activo",
                        columnNames = {"paciente_id", "dedo", "activo"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconocimientoHuella {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(name = "dedo", nullable = false, length = 30)
    private DedoHuella dedo;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "template_biometrico", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] templateBiometrico;

    @Column(name = "ancho_imagen", nullable = false)
    private Integer anchoImagen;

    @Column(name = "alto_imagen", nullable = false)
    private Integer altoImagen;

    @Column(name = "dpi", nullable = false)
    private Integer dpi;

    @Column(name = "calidad_captura", length = 50)
    private String calidadCaptura;

    @Column(name = "proveedor", nullable = false, length = 100)
    private String proveedor;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_captura", nullable = false)
    private Date fechaCaptura;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_vinculo_paciente", nullable = false)
    private Date fechaVinculoPaciente;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "imagen_preview", columnDefinition = "LONGBLOB")
    private byte[] imagenPreview;

    @PrePersist
    public void prePersist() {
        Date ahora = new Date();
        if (activo == null) {
            activo = true;
        }
        if (fechaCaptura == null) {
            fechaCaptura = ahora;
        }
        if (fechaVinculoPaciente == null) {
            fechaVinculoPaciente = ahora;
        }
        if (proveedor == null || proveedor.isBlank()) {
            proveedor = "SOURCE_AFIS_LOCAL";
        }
    }
}
