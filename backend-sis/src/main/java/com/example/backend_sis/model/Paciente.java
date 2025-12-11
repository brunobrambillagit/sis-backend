package com.example.backend_sis.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Atributos básicos
    @Column(unique = true, nullable = false)
    private String dni; // Llave única y obligatoria para buscar.

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    private Integer edad;
    private String sexo;

    private String nroHistoriaClinica;

    @Enumerated(EnumType.STRING)
    private EstadoPersona estadoPersona;

    @Enumerated(EnumType.STRING)
    private EstadoAtencion estadoAtencion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    // Relación
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<Reconocimiento> reconocimientos;

    // Enumeraciones
    public enum EstadoPersona {
        VIVO, FALLECIDO
    }

    public enum EstadoAtencion {
        EN_ATENCION, EN_ESPERA, ALTA
    }

    // ------------------------------------
    // 1. CONSTRUCTORES
    // ------------------------------------

    // Constructor vacío (necesario para JPA/Hibernate)
    public Paciente() {
    }

    // Constructor con atributos principales (sin 'id' y sin 'reconocimientos'
    // que generalmente se inicializa con una lista vacía o se añade después)
    public Paciente(String dni, String nombre, String apellido, Date fechaNacimiento, Integer edad, String sexo, String nroHistoriaClinica, EstadoPersona estadoPersona, EstadoAtencion estadoAtencion, Date fechaAlta, Date fechaModificacion) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.edad = edad;
        this.sexo = sexo;
        this.nroHistoriaClinica = nroHistoriaClinica;
        this.estadoPersona = estadoPersona;
        this.estadoAtencion = estadoAtencion;
        this.fechaAlta = fechaAlta;
        this.fechaModificacion = fechaModificacion;
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNroHistoriaClinica() {
        return nroHistoriaClinica;
    }

    public void setNroHistoriaClinica(String nroHistoriaClinica) {
        this.nroHistoriaClinica = nroHistoriaClinica;
    }

    public EstadoPersona getEstadoPersona() {
        return estadoPersona;
    }

    public void setEstadoPersona(EstadoPersona estadoPersona) {
        this.estadoPersona = estadoPersona;
    }

    public EstadoAtencion getEstadoAtencion() {
        return estadoAtencion;
    }

    public void setEstadoAtencion(EstadoAtencion estadoAtencion) {
        this.estadoAtencion = estadoAtencion;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public List<Reconocimiento> getReconocimientos() {
        return reconocimientos;
    }

    public void setReconocimientos(List<Reconocimiento> reconocimientos) {
        this.reconocimientos = reconocimientos;
    }
}