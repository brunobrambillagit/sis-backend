package com.example.backend_sis.dto;

import com.example.backend_sis.model.Paciente;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
public class PacienteRostroMatchResponse {
    private Long id;
    private String dni;
    private String nombre;
    private String apellido;
    private String nroHistoriaClinica;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private Paciente.tipoSexo sexo;
    private Paciente.EstadoPersona estadoPersona;
    private Date fechaAlta;
    private Date fechaModificacion;
    private String referenciaBiometrica;
    private Float similitud;
}
