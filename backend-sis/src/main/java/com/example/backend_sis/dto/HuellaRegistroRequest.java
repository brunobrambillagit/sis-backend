package com.example.backend_sis.dto;

import com.example.backend_sis.model.DedoHuella;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HuellaRegistroRequest {

    @NotNull(message = "El pacienteId es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El dedo es obligatorio")
    private DedoHuella dedo;

    @NotBlank(message = "La imagen RAW en Base64 es obligatoria")
    private String rawImageBase64;

    @NotNull(message = "El ancho de imagen es obligatorio")
    @Min(value = 1, message = "El ancho debe ser mayor a 0")
    private Integer width;

    @NotNull(message = "El alto de imagen es obligatorio")
    @Min(value = 1, message = "El alto debe ser mayor a 0")
    private Integer height;

    @NotNull(message = "El DPI es obligatorio")
    @Min(value = 1, message = "El DPI debe ser mayor a 0")
    private Integer dpi;

    private String quality;
}
