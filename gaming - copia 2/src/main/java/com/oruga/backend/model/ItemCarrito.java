package com.oruga.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad ItemCarrito - Replica del modelo Android
 * Representa juegos agregados al carrito de compras
 */
@Entity
@Table(name = "carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Integer juegoId;

    @Column(nullable = false)
    private String nombreJuego;

    // En CLP (Pesos Chilenos)
    @Column(nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Integer cantidad = 1;

    @Column(nullable = false)
    private Integer imagenUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaAgregado;

    /**
     * Calcular subtotal
     */
    public Double calcularSubtotal() {
        return precioUnitario * cantidad;
    }
}