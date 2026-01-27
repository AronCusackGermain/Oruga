package com.example.oruga.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Publicacion - Compatible con Room Database
 */
@Entity
@Table(name = "publicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer autorId;

    @NotBlank
    @Column(nullable = false)
    private String autorNombre;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String contenido;

    private String imagenUrl = "";

    @Column(length = 2000)
    private String imagenesUrls = "";

    @Column(nullable = false)
    private Long fechaPublicacion = System.currentTimeMillis();

    @Column(nullable = false)
    private Integer cantidadLikes = 0;

    @Column(nullable = false)
    private Integer cantidadComentarios = 0;

    @Column(nullable = false)
    private Boolean esAnuncio = false;
}