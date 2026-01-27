package com.oruga.gaming.dto;

import com.oruga.gaming.entity.TipoMensaje;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MensajeDto {
    private Long id;
    private Long remitenteId;
    private String remitenteNombre;
    private Long destinatarioId;
    private String contenido;
    private String imagenUrl;
    private LocalDateTime fechaEnvio;
    private Boolean esGrupal;
    private Boolean leido;
    private TipoMensaje tipoMensaje;
    private String tiempoRelativo;
}
