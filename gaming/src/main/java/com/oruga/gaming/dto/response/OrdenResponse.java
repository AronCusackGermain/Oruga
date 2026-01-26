package com.oruga.gaming.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenResponse {
    
    private Long id;
    private String numeroOrden;
    private Long usuarioId;
    private String items; // JSON
    private BigDecimal subtotal;
    private String subtotalFormateado;
    private BigDecimal descuento;
    private String descuentoFormateado;
    private BigDecimal total;
    private String totalFormateado;
    private String estado;
    private String estadoDescripcion;
    private String metodoPago;
    private String comprobanteUrl;
    private LocalDateTime fechaCreacion;
    private String fechaCreacionFormateada;
    private LocalDateTime fechaRevision;
    private String comentarioModerador;
}
