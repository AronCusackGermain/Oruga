package com.oruga.gaming.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeRequest {
    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;
    
    private String imagenUrl;
    
    private Long destinatarioId; // Null para mensajes grupales
}
