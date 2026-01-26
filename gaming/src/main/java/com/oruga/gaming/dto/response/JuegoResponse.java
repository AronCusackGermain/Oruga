package com.oruga.gaming.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JuegoResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private String genero;
    private BigDecimal precio;
    private String precioFormateado;
    private Integer stock;
    private String imagenUrl;
    private String plataformas;
    private String desarrollador;
    private String fechaLanzamiento;
    private BigDecimal calificacion;
    private Boolean activo;
}
