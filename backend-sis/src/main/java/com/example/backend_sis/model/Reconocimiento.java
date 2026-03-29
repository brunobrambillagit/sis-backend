package com.example.backend_sis.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(
        name = "reconocimiento",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_reconocimiento_face_id", columnNames = "referenciaBiometrica")
        }
)
public class Reconocimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String referenciaBiometrica;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoBiometria tipoBiometria;

    @Column(length = 255)
    private String proveedor;

    @Column(length = 255)
    private String collectionId;

    @Column(length = 255)
    private String externalImageId;

    @Column(length = 255)
    private String s3Bucket;

    @Column(length = 500)
    private String s3Key;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaCaptura;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaVinculoPaciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    public enum TipoBiometria {
        HUELLA_DACTILAR,
        ROSTRO
    }

    public Reconocimiento() {
    }

    public Long getId() {
        return id;
    }

    public String getReferenciaBiometrica() {
        return referenciaBiometrica;
    }

    public void setReferenciaBiometrica(String referenciaBiometrica) {
        this.referenciaBiometrica = referenciaBiometrica;
    }

    public TipoBiometria getTipoBiometria() {
        return tipoBiometria;
    }

    public void setTipoBiometria(TipoBiometria tipoBiometria) {
        this.tipoBiometria = tipoBiometria;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getExternalImageId() {
        return externalImageId;
    }

    public void setExternalImageId(String externalImageId) {
        this.externalImageId = externalImageId;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public Date getFechaCaptura() {
        return fechaCaptura;
    }

    public void setFechaCaptura(Date fechaCaptura) {
        this.fechaCaptura = fechaCaptura;
    }

    public Date getFechaVinculoPaciente() {
        return fechaVinculoPaciente;
    }

    public void setFechaVinculoPaciente(Date fechaVinculoPaciente) {
        this.fechaVinculoPaciente = fechaVinculoPaciente;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}
