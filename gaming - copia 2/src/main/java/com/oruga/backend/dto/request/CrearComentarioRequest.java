package com.oruga.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearComentarioRequest {
    @NotNull(message = "La publicación es obligatoria")
    private Long publicacionId;

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;
}
