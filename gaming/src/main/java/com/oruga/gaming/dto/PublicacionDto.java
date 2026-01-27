package com.oruga.gaming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicacionDto {
    private Long id;
    private String titulo;
    private String contenido;
    private Long autorId;
    private String autorNombre;
    private Integer likes;
    private Integer cantidadComentarios;
    private String fechaCreacion;
    private String tiempoRelativo;
    private Boolean esAnuncio;
}
