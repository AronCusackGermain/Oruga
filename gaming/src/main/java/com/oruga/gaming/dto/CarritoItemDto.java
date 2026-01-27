package com.oruga.gaming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarritoItemDto {
    private Long id;
    private Long juegoId;
    private String nombre;
    private Double precioUnitario;
    private Integer cantidad;
    private Double subtotal;
    private Integer stockDisponible;
}
