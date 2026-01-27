package com.oruga.backend.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCantidadRequest {
    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidad;
}
