package com.oruga.gaming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JuegoDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String genero;
    private Double precio;
    private String precioFormateado;
    private Integer stock;
    private String imagenUrl;
    private String plataformas;
    private String desarrollador;
    private String fechaLanzamiento;
    private Double calificacion;
    private Boolean activo;
}
