package com.example.backend_sis.model;

import com.example.backend_sis.model.TipoMovimientoCama;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_cama")
@Getter
@Setter
@NoArgsConstructor
public class MovimientoCama {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "episodio_id", nullable = false)
    private Episodio episodio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cama_origen_id")
    private Cama camaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cama_destino_id")
    private Cama camaDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimientoCama tipoMovimiento;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;
}