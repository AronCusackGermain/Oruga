package com.oruga.gaming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Orden - Órdenes de compra con flujo de pago por transferencia
 */
@Entity
@Table(name = "ordenes")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orden", unique = true, nullable = false, length = 50)
    private String numeroOrden;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(columnDefinition = "JSON", nullable = false)
    private String items; // JSON con los items de la orden

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(length = 30)
    private String estado = "pendiente_pago"; // pendiente_pago, en_revision, aprobada, rechazada, cancelada

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago = "transferencia";

    @Column(name = "comprobante_url", length = 500)
    private String comprobanteUrl;

    @Column(name = "fecha_subida_comprobante")
    private LocalDateTime fechaSubidaComprobante;

    @Column(name = "moderador_id")
    private Long moderadorId;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "comentario_moderador", columnDefinition = "TEXT")
    private String comentarioModerador;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Marca la orden como en revisión después de subir comprobante
     */
    public void marcarEnRevision(String urlComprobante) {
        this.comprobanteUrl = urlComprobante;
        this.fechaSubidaComprobante = LocalDateTime.now();
        this.estado = "en_revision";
    }

    /**
     * Aprueba la orden
     */
    public void aprobar(Long moderadorId, String comentario) {
        this.estado = "aprobada";
        this.moderadorId = moderadorId;
        this.comentarioModerador = comentario;
        this.fechaRevision = LocalDateTime.now();
    }

    /**
     * Rechaza la orden
     */
    public void rechazar(Long moderadorId, String comentario) {
        this.estado = "rechazada";
        this.moderadorId = moderadorId;
        this.comentarioModerador = comentario;
        this.fechaRevision = LocalDateTime.now();
    }

    /**
     * Cancela la orden
     */
    public void cancelar() {
        this.estado = "cancelada";
    }

    /**
     * Formatea el total en CLP
     */
    public String getTotalFormateado() {
        return String.format("$%,.0f", total);
    }

    /**
     * Verifica si la orden está pendiente de revisión
     */
    public boolean estaPendienteRevision() {
        return "en_revision".equals(estado);
    }
}
