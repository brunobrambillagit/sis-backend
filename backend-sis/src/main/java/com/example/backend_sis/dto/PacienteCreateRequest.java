package com.example.backend_sis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacienteCreateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 120)
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(max = 20)
    private String dni;
}