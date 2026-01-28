package com.oruga.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Publicacion - Replica del modelo Android
 * Representa publicaciones en la comunidad con soporte para múltiples imágenes
 */
@Entity
@Table(name = "publicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(nullable = false)
    private String autorNombre;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "El contenido es obligatorio")
    @Column(nullable = false, length = 5000)
    private String contenido;

    // URL de imagen principal
    @Column(length = 500)
    private String imagenUrl = "";

    // Lista de URLs separadas por comas
    @Column(length = 2000)
    private String imagenesUrls = "";

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaPublicacion;

    @Column(nullable = false)
    private Integer cantidadLikes = 0;

    @Column(nullable = false)
    private Integer cantidadComentarios = 0;

    @Column(nullable = false)
    private Boolean esAnuncio = false;

    /**
     * Incrementar likes
     */
    public void incrementarLikes() {
        this.cantidadLikes++;
    }

    /**
     * Incrementar comentarios
     */
    public void incrementarComentarios() {
        this.cantidadComentarios++;
    }

    @PrePersist
    protected void onCreate() {
        if (imagenUrl == null) {
            imagenUrl = "";
        }
        if (imagenesUrls == null) {
            imagenesUrls = "";
        }
        if (cantidadLikes == null) {
            cantidadLikes = 0;
        }
        if (cantidadComentarios == null) {
            cantidadComentarios = 0;  // ✅ Esto soluciona el error
        }
        if (esAnuncio == null) {
            esAnuncio = false;
        }
    }
}
