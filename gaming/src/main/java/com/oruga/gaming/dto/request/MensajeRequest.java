package com.oruga.gaming.dto.request;

import lombok.Data;

@Data
public class MensajeRequest {
    
    private Long destinatarioId;
    private String contenido;
}
