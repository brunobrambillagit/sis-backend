package com.example.backend_sis.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reconocimiento")
public class Reconocimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Atributos basados en el Diagrama de Clases (Reconocimiento/Biometria)

    // Aquí se guardará la plantilla (el "hash") de la huella o el ID de AWS Rekognition para el rostro.
    // Usamos LOB (Large Object) para guardar datos binarios grandes.
    @Lob
    @Column(nullable = false)
    private byte[] hashBiometria; // hashBiometria

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBiometria tipoBiometria; // tipoBiometria: Reconocimiento (Huella o Rostro)

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaCaptura; // fechaCaptura

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaVinculoPaciente; // fechaVinculoPaciente

    // Relación con Paciente. Cardinalidad * a 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // Enumeración del tipo de Biometría
    public enum TipoBiometria {
        HUELLA_DACTILAR, ROSTRO
    }

    // ------------------------------------
    // 1. CONSTRUCTORES
    // ------------------------------------

    // Constructor vacío (necesario para JPA/Hibernate)
    public Reconocimiento() {
    }

    // Constructor con todos los atributos (excepto 'id' que es generado)
    public Reconocimiento(byte[] hashBiometria, TipoBiometria tipoBiometria, Date fechaCaptura, Date fechaVinculoPaciente, Paciente paciente) {
        this.hashBiometria = hashBiometria;
        this.tipoBiometria = tipoBiometria;
        this.fechaCaptura = fechaCaptura;
        this.fechaVinculoPaciente = fechaVinculoPaciente;
        this.paciente = paciente;
    }

    // ------------------------------------
    // 2. GETTERS Y SETTERS
    // ------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getHashBiometria() {
        return hashBiometria;
    }

    public void setHashBiometria(byte[] hashBiometria) {
        this.hashBiometria = hashBiometria;
    }

    public TipoBiometria getTipoBiometria() {
        return tipoBiometria;
    }

    public void setTipoBiometria(TipoBiometria tipoBiometria) {
        this.tipoBiometria = tipoBiometria;
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

    // ------------------------------------
    // 3. ENUM (Mantenido)
    // ------------------------------------
    /*
    public enum TipoBiometria {
        HUELLA_DACTILAR, ROSTRO
    }
    */
}