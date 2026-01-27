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
public class ComentarioResponse {
    private Long id;
    private String autorNombre;
    private String contenido;
    private LocalDateTime fechaComentario;
}