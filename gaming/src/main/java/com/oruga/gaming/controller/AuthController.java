package com.oruga.gaming.controller;

import com.oruga.gaming.dto.JwtResponse;
import com.oruga.gaming.dto.ApiResponse;
import com.oruga.gaming.dto.LoginRequest;
import com.oruga.gaming.dto.RegisterRequest;
import com.oruga.gaming.dto.UsuarioDto;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.security.JwtTokenProvider;
import com.oruga.gaming.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        try {
            // Verificar si el email ya existe
            if (usuarioService.existeEmail(request.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(ApiResponse.error("El email ya está registrado"));
            }

            // Registrar usuario
            Usuario usuario = usuarioService.registrarUsuario(request);

            // Autenticar automáticamente
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Crear response
            UsuarioDto usuarioDto = usuarioService.toDto(usuario);
            JwtResponse jwtResponse = JwtResponse.builder()
                    .token(jwt)
                    .type("Bearer")
                    .usuario(usuarioDto)
                    .build();

            String mensaje = usuario.getEsModerador()
                    ? "Registro exitoso. Bienvenido Moderador!"
                    : "Registro exitoso. Bienvenido!";

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(jwtResponse, mensaje));

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error al registrar: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            // Verificar si el usuario existe
            Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());
            if (usuario == null) {
                return ResponseEntity
                        .badRequest()
                        .body(ApiResponse.error("No existe una cuenta con este email"));
            }

            // Verificar si está baneado
            if (usuario.getEstaBaneado()) {
                String mensaje = usuario.getRazonBaneo() != null
                        ? "Cuenta suspendida: " + usuario.getRazonBaneo()
                        : "Tu cuenta ha sido suspendida";
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(mensaje));
            }

            // Autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Actualizar estado de conexión
            usuarioService.actualizarEstadoConexion(usuario.getId(), true);

            // Crear response
            UsuarioDto usuarioDto = usuarioService.toDto(usuario);
            JwtResponse jwtResponse = JwtResponse.builder()
                    .token(jwt)
                    .type("Bearer")
                    .usuario(usuarioDto)
                    .build();

            return ResponseEntity.ok(
                    ApiResponse.success(jwtResponse, "Bienvenido " + usuario.getNombreUsuario())
            );

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Credenciales inválidas"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String token) {

        try {
            // Extraer email del token
            String jwt = token.substring(7); // Remover "Bearer "
            String email = tokenProvider.getUsernameFromToken(jwt);

            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario != null) {
                usuarioService.actualizarEstadoConexion(usuario.getId(), false);
            }

            return ResponseEntity.ok(
                    ApiResponse.success(null, "Sesión cerrada correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Sesión cerrada")
            );
        }
    }
}