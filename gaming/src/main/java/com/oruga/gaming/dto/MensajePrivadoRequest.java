package com.oruga.gaming.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajePrivadoRequest {
    @NotNull(message = "El ID del destinatario es obligatorio")
    private Long destinatarioId;
    
    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;
    
    private String imagenUrl;
}
