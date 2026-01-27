package com.oruga.gaming.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgregarCarritoRequest {
    @NotNull(message = "El ID del juego es obligatorio")
    private Long juegoId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad = 1;
}
