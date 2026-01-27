package com.oruga.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionResponse {
    private Long id;
    private String autorNombre;
    private String titulo;
    private String contenido;
    private String imagenUrl;
    private String imagenesUrls;
    private LocalDateTime fechaPublicacion;
    private Integer cantidadLikes;
    private Integer cantidadComentarios;
    private Boolean esAnuncio;
}