package com.oruga.gaming.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgregarCarritoRequest {
    
    @NotNull(message = "El ID del juego es obligatorio")
    private Long juegoId;
    
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad = 1;
}
