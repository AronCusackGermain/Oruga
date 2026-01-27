package com.oruga.gaming.dto;

import com.oruga.gaming.entity.TipoMensaje;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

// ========== REQUEST DTOs ==========

// ========== RESPONSE DTOs ==========

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversacionDto {
    private Long usuarioId;
    private String nombreUsuario;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
    private Integer mensajesNoLeidos;
    private Boolean estaConectado;
}
