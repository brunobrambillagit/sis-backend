package com.example.backend_sis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodioCambioCamaRequest {
    private Long nuevaCamaId;
    private Long usuarioId;
}
