package com.oruga.backend.repository;

import com.oruga.backend.model.Reporte;
import com.oruga.backend.model.enums.EstadoReporte;
import com.oruga.backend.model.enums.TipoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByEstadoOrderByFechaReporteDesc(EstadoReporte estado);
    List<Reporte> findByTipoReporteOrderByFechaReporteDesc(TipoReporte tipo);
    List<Reporte> findAllByOrderByFechaReporteDesc();
    Long countByEstado(EstadoReporte estado);
}