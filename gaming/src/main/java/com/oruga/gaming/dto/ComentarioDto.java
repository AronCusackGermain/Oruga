package com.oruga.gaming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioDto {
    private Long id;
    private String contenido;
    private Long autorId;
    private String autorNombre;
    private String fechaCreacion;
}
