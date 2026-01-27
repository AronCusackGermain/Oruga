package com.oruga.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarCarritoRequest {
    @NotNull(message = "El ID del juego es obligatorio")
    private Integer juegoId;

    @NotBlank(message = "El nombre del juego es obligatorio")
    private String nombreJuego;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private Double precioUnitario;

    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidad = 1;

    @NotNull(message = "La imagen URL es obligatoria")
    private Integer imagenUrl;
}
