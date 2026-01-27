package com.oruga.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnviarMensajePrivadoRequest {
    private Long destinatarioId;

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;

    private String imagenUrl;
}