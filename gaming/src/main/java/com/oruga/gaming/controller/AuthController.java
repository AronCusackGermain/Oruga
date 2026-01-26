package com.oruga.gaming.controller;

import com.oruga.gaming.dto.request.LoginRequest;
import com.oruga.gaming.dto.request.RegisterRequest;
import com.oruga.gaming.dto.response.ApiResponse;
import com.oruga.gaming.dto.response.JwtResponse;
import com.oruga.gaming.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticación (registro y login)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Registra un nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        JwtResponse response = authService.register(request);
        return ResponseEntity.ok(
                ApiResponse.success("Usuario registrado exitosamente", response)
        );
    }

    /**
     * POST /api/auth/login
     * Inicia sesión y devuelve token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Login exitoso", response)
        );
    }

    /**
     * POST /api/auth/logout
     * Cierra sesión del usuario (actualiza estado de conexión)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");

        authService.logout(usuarioId);
        return ResponseEntity.ok(
                ApiResponse.success("Logout exitoso", null)
        );
    }
}