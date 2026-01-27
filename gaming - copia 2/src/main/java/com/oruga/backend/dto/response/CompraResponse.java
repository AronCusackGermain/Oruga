package com.oruga.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; /**
 * DTO para respuesta de compra
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponse {
    private String mensaje;
    private Double total;
    private Integer cantidadItems;
}
