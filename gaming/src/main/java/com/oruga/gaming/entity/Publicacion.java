package com.oruga.gaming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Publicacion - Representa posts en el foro con soporte para imágenes
 */
@Entity
@Table(name = "publicaciones")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "autor_id", nullable = false)
    private Long autorId;

    @Column(name = "autor_nombre", nullable = false, length = 50)
    private String autorNombre;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion = LocalDateTime.now();

    @Column(name = "cantidad_likes")
    private Integer cantidadLikes = 0;

    @Column(name = "cantidad_comentarios")
    private Integer cantidadComentarios = 0;

    @Column(name = "es_anuncio")
    private Boolean esAnuncio = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Incrementa el contador de likes
     */
    public void incrementarLikes() {
        this.cantidadLikes++;
    }

    /**
     * Incrementa el contador de comentarios
     */
    public void incrementarComentarios() {
        this.cantidadComentarios++;
    }

    /**
     * Decrementa el contador de comentarios
     */
    public void decrementarComentarios() {
        if (this.cantidadComentarios > 0) {
            this.cantidadComentarios--;
        }
    }

    /**
     * Verifica si la publicación tiene imagen
     */
    public boolean tieneImagen() {
        return imagenUrl != null && !imagenUrl.isEmpty();
    }
}
