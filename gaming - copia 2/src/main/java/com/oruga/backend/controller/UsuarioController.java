package com.oruga.backend.controller;

import com.oruga.backend.dto.request.ActualizarPerfilRequest;
import com.oruga.backend.dto.response.UsuarioResponse;
import com.oruga.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Usuarios
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Obtener perfil del usuario actual autenticado
     * GET /api/usuarios/perfil
     */
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponse> obtenerPerfil(Authentication auth) {
        return ResponseEntity.ok(usuarioService.obtenerPerfilActual(auth));
    }

    /**
     * Obtener usuario por ID
     * GET /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    /**
     * Obtener todos los usuarios
     * GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    /**
     * Obtener usuarios por estado de conexión
     * GET /api/usuarios/conectados?estado=true
     */
    @GetMapping("/conectados")
    public ResponseEntity<List<UsuarioResponse>> obtenerPorEstado(
            @RequestParam Boolean estado
    ) {
        return ResponseEntity.ok(usuarioService.obtenerPorEstadoConexion(estado));
    }

    /**
     * Obtener usuarios baneados
     * GET /api/usuarios/baneados
     */
    @GetMapping("/baneados")
    public ResponseEntity<List<UsuarioResponse>> obtenerBaneados() {
        return ResponseEntity.ok(usuarioService.obtenerUsuariosBaneados());
    }

    /**
     * Obtener moderadores
     * GET /api/usuarios/moderadores
     */
    @GetMapping("/moderadores")
    public ResponseEntity<List<UsuarioResponse>> obtenerModeradores() {
        return ResponseEntity.ok(usuarioService.obtenerModeradores());
    }

    /**
     * Actualizar perfil del usuario actual
     * PUT /api/usuarios/perfil
     */
    @PutMapping("/perfil")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @Valid @RequestBody ActualizarPerfilRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(request, auth));
    }

    /**
     * Conectar cuentas externas (Steam, Discord)
     * PUT /api/usuarios/conexiones
     */
    @PutMapping("/conexiones")
    public ResponseEntity<UsuarioResponse> conectarCuentasExternas(
            @RequestParam(required = false) String steamId,
            @RequestParam(required = false) String discordId,
            Authentication auth
    ) {
        return ResponseEntity.ok(
                usuarioService.conectarCuentasExternas(steamId, discordId, auth)
        );
    }

    /**
     * Actualizar estado de conexión de un usuario
     * PUT /api/usuarios/{id}/estado
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Boolean conectado
    ) {
        usuarioService.actualizarEstadoConexion(id, conectado);
        return ResponseEntity.ok().build();
    }

    /**
     * Cerrar sesión (logout)
     * POST /api/usuarios/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication auth) {
        usuarioService.logout(auth);
        return ResponseEntity.ok().build();
    }
}