package com.oruga.gaming.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

// ========== REQUEST DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}

// ========== RESPONSE DTOs ==========

// ========== JUEGO DTOs ==========

// ========== CARRITO DTOs ==========

// ========== PUBLICACION DTOs ==========

