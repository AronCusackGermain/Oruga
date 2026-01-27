package com.oruga.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estadísticas de usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasUsuarioResponse {
    private Integer cantidadPublicaciones;
    private Integer cantidadMensajes;
    private Integer cantidadReportes;
}