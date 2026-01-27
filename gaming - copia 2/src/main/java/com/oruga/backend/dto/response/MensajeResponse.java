package com.oruga.backend.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponse {
    private Long id;
    private String remitenteNombre;
    private String contenido;
    private String imagenUrl;
    private LocalDateTime fechaEnvio;
    private Boolean esGrupal;
    private String tipoMensaje;
}