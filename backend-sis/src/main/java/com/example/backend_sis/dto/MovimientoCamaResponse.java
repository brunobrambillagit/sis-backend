package com.example.backend_sis.dto;

import com.example.backend_sis.model.TipoMovimientoCama;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCamaResponse {

    private Long id;
    private Long episodioId;

    private Long camaOrigenId;
    private String camaOrigenCodigo;

    private Long camaDestinoId;
    private String camaDestinoCodigo;

    private Long usuarioId;
    private String usuarioNombreCompleto;

    private TipoMovimientoCama tipoMovimiento;
    private LocalDateTime fechaMovimiento;
}