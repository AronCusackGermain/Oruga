package com.oruga.gaming.dto.response;

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
    private Long autorId;
    private String autorNombre;
    private String titulo;
    private String contenido;
    private String imagenUrl;
    private LocalDateTime fechaPublicacion;
    private String fechaPublicacionFormateada;
    private Integer cantidadLikes;
    private Integer cantidadComentarios;
    private Boolean esAnuncio;
}
