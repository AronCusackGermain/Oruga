package com.example.oruga.controller;

import com.example.oruga.model.Usuario;
import com.example.oruga.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para Usuario
 * Base URL: /api/usuarios
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permitir peticiones desde Android
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * POST /api/usuarios/registro
     * Registrar nuevo usuario
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.registrar(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registro exitoso");
            response.put("usuario", nuevoUsuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/usuarios/login
     * Iniciar sesión
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            Usuario usuario = usuarioService.login(email, password);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("usuario", usuario);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * POST /api/usuarios/{id}/logout
     * Cerrar sesión
     */
    @PostMapping("/{id}/logout")
    public ResponseEntity<?> logout(@PathVariable Integer id) {
        try {
            usuarioService.logout(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logout exitoso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/usuarios
     * Obtener todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/usuarios/{id}
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/usuarios/conectados
     * Obtener usuarios conectados
     */
    @GetMapping("/conectados")
    public ResponseEntity<List<Usuario>> obtenerConectados() {
        List<Usuario> usuarios = usuarioService.obtenerConectados();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/usuarios/baneados
     * Obtener usuarios baneados (solo moderadores)
     */
    @GetMapping("/baneados")
    public ResponseEntity<List<Usuario>> obtenerBaneados() {
        List<Usuario> usuarios = usuarioService.obtenerBaneados();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * PUT /api/usuarios/{id}
     * Actualizar usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        try {
            usuario.setId(id);
            Usuario actualizado = usuarioService.actualizar(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario actualizado");
            response.put("usuario", actualizado);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/usuarios/{id}/banear
     * Banear usuario (solo moderadores)
     */
    @PostMapping("/{id}/banear")
    public ResponseEntity<?> banear(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String razon = body.getOrDefault("razon", "Sin razón especificada");
            usuarioService.banearUsuario(id, razon);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario baneado");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/usuarios/{id}/desbanear
     * Desbanear usuario (solo moderadores)
     */
    @PostMapping("/{id}/desbanear")
    public ResponseEntity<?> desbanear(@PathVariable Integer id) {
        try {
            usuarioService.desbanearUsuario(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario desbaneado");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}