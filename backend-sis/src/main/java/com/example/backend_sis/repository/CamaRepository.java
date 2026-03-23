package com.example.backend_sis.repository;

import com.example.backend_sis.model.Cama;
import com.example.backend_sis.model.EstadoCama;
import com.example.backend_sis.model.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CamaRepository extends JpaRepository<Cama, Long> {

    List<Cama> findByTipoServicioAndEstadoOrderByCodigoAsc(TipoServicio tipoServicio, EstadoCama estado);
}
