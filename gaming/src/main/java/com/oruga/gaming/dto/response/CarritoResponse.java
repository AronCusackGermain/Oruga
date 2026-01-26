package com.oruga.gaming.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponse {
    
    private Long id;
    private List<CarritoItemResponse> items;
    private Integer cantidadItems;
    private BigDecimal subtotal;
    private String subtotalFormateado;
    private BigDecimal descuento;
    private String descuentoFormateado;
    private BigDecimal total;
    private String totalFormateado;
    private Boolean tieneDescuentoModerador;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarritoItemResponse {
        private Long id;
        private Long juegoId;
        private String nombre;
        private String descripcion;
        private String imagenUrl;
        private BigDecimal precioUnitario;
        private String precioUnitarioFormateado;
        private Integer cantidad;
        private Integer stockDisponible;
        private BigDecimal subtotal;
        private String subtotalFormateado;
    }
}
