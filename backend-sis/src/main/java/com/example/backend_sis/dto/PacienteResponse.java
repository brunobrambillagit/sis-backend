package com.example.backend_sis.dto;

import com.example.backend_sis.model.Paciente;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PacienteResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String nroHistoriaClinica;
    private Date fechaNacimiento;
    private Integer edad;
    private Paciente.tipoSexo sexo;
    private Paciente.EstadoPersona estadoPersona;
    private Date fechaAlta;
    private Date fechaModificacion;
}
