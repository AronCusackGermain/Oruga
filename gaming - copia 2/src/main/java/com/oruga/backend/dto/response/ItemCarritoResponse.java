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
public class ItemCarritoResponse {
    private Long id;
    private Long juegoId;
    private String nombreJuego;
    private Double precioUnitario;
    private Integer cantidad;
    private String imagenUrl;
    private Double subtotal;
    private LocalDateTime fechaAgregado;
}