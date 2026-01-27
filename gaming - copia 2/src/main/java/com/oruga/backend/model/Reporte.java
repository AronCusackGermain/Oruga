package com.oruga.backend.model;

import com.oruga.backend.model.enums.EstadoReporte;
import com.oruga.backend.model.enums.TipoReporte;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Reporte - Replica del modelo Android
 * Modelo para reportes de contenido inapropiado
 */
@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que reporta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportado_por_id", nullable = false)
    private Usuario reportadoPor;

    @Column(nullable = false)
    private String reportadoPorNombre;

    // Tipo de reporte
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoReporte tipoReporte;

    @Column(nullable = false)
    private Integer idContenido; // ID de publicación o mensaje

    // Detalles
    @Column(nullable = false)
    private String razon;

    @Column(length = 2000)
    private String descripcion = "";

    // Estado
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReporte estado = EstadoReporte.PENDIENTE;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaReporte;

    // Resolución (por moderador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderador_id")
    private Usuario moderador;

    @Column
    private String moderadorNombre;

    @Column
    private LocalDateTime fechaResolucion;

    @Column(length = 2000)
    private String accionTomada = "";
}