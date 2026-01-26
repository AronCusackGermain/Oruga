package com.oruga.gaming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Juego - Catálogo de videojuegos con precios en CLP
 */
@Entity
@Table(name = "juegos")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 100)
    private String genero;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio; // Precio en CLP

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "imagen_url")
    private String imagenUrl;

    private String plataformas;

    private String desarrollador;

    @Column(name = "fecha_lanzamiento", length = 50)
    private String fechaLanzamiento;

    @Column(precision = 3, scale = 1)
    private BigDecimal calificacion = BigDecimal.ZERO;

    private Boolean activo = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Formatea el precio en formato CLP
     */
    public String getPrecioFormateado() {
        return String.format("$%,.0f", precio);
    }

    /**
     * Verifica si hay stock disponible
     */
    public boolean tieneStock() {
        return stock != null && stock > 0;
    }

    /**
     * Reduce el stock después de una compra
     */
    public void reducirStock(int cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
        } else {
            throw new IllegalStateException("Stock insuficiente");
        }
    }
}
