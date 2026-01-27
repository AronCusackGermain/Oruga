package com.oruga.gaming.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioRequest {
    @NotBlank(message = "El contenido es obligatorio")
    @Size(min = 1, max = 1000)
    private String contenido;
}
