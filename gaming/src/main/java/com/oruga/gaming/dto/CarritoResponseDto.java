package com.oruga.gaming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarritoResponseDto {
    private Long id;
    private java.util.List<CarritoItemDto> items;
    private Integer cantidadItems;
    private Double subtotal;
    private String subtotalFormateado;
    private Double descuento;
    private String descuentoFormateado;
    private Double total;
    private String totalFormateado;
    private Boolean tieneDescuentoModerador;
}
