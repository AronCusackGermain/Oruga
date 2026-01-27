package com.oruga.gaming.controller;

import com.oruga.gaming.dto.ApiResponse;
import com.oruga.gaming.dto.UsuarioDto;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtener perfil del usuario autenticado
     */
    @GetMapping("/perfil")
    public ResponseEntity<ApiResponse<UsuarioDto>> obtenerPerfil(
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email);

            if (usuario == null) {
                return ResponseEntity
                        .badRequest()
                        .body(ApiResponse.error("Usuario no encontrado"));
            }

            UsuarioDto usuarioDto = usuarioService.toDto(usuario);
            return ResponseEntity.ok(
                    ApiResponse.success(usuarioDto, "Perfil obtenido correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDto>> obtenerUsuarioPorId(
            @PathVariable Long id) {

        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            UsuarioDto usuarioDto = usuarioService.toDto(usuario);

            return ResponseEntity.ok(
                    ApiResponse.success(usuarioDto, "Usuario encontrado")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Usuario no encontrado"));
        }
    }

    /**
     * Obtener todos los usuarios
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioDto>>> obtenerTodosUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodosUsuarios();
            List<UsuarioDto> usuariosDto = usuarios.stream()
                    .map(usuarioService::toDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.success(usuariosDto, "Usuarios obtenidos correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener usuarios por estado de conexión
     */
    @GetMapping("/estado/{conectado}")
    public ResponseEntity<ApiResponse<List<UsuarioDto>>> obtenerPorEstado(
            @PathVariable Boolean conectado) {

        try {
            List<Usuario> usuarios = usuarioService.obtenerPorEstadoConexion(conectado);
            List<UsuarioDto> usuariosDto = usuarios.stream()
                    .map(usuarioService::toDto)
                    .collect(Collectors.toList());

            String mensaje = conectado ? "Usuarios conectados" : "Usuarios desconectados";
            return ResponseEntity.ok(
                    ApiResponse.success(usuariosDto, mensaje)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener usuarios baneados
     */
    @GetMapping("/baneados")
    public ResponseEntity<ApiResponse<List<UsuarioDto>>> obtenerBaneados() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerUsuariosBaneados();
            List<UsuarioDto> usuariosDto = usuarios.stream()
                    .map(usuarioService::toDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.success(usuariosDto, "Usuarios baneados obtenidos")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Actualizar descripción del perfil
     */
    @PutMapping("/perfil/descripcion")
    public ResponseEntity<ApiResponse<UsuarioDto>> actualizarDescripcion(
            @RequestParam String descripcion,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email);

            usuario.setDescripcion(descripcion);
            usuarioService.actualizarUsuario(usuario);

            UsuarioDto usuarioDto = usuarioService.toDto(usuario);
            return ResponseEntity.ok(
                    ApiResponse.success(usuarioDto, "Descripción actualizada")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Actualizar conexiones externas (Steam, Discord)
     */
    @PutMapping("/perfil/conexiones")
    public ResponseEntity<ApiResponse<UsuarioDto>> actualizarConexiones(
            @RequestParam(required = false) String steamId,
            @RequestParam(required = false) String discordId,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email);

            if (steamId != null) {
                usuario.setSteamId(steamId);
            }
            if (discordId != null) {
                usuario.setDiscordId(discordId);
            }

            usuarioService.actualizarUsuario(usuario);

            UsuarioDto usuarioDto = usuarioService.toDto(usuario);
            return ResponseEntity.ok(
                    ApiResponse.success(usuarioDto, "Conexiones actualizadas")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Banear usuario (solo moderadores)
     */
    @PutMapping("/{id}/banear")
    public ResponseEntity<ApiResponse<Void>> banearUsuario(
            @PathVariable Long id,
            @RequestParam String razon,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Usuario moderador = usuarioService.buscarPorEmail(email);

            if (!moderador.getEsModerador()) {
                return ResponseEntity
                        .status(403)
                        .body(ApiResponse.error("No tienes permisos de moderador"));
            }

            usuarioService.banearUsuario(id, true, razon);

            return ResponseEntity.ok(
                    ApiResponse.success(null, "Usuario baneado correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Desbanear usuario (solo moderadores)
     */
    @PutMapping("/{id}/desbanear")
    public ResponseEntity<ApiResponse<Void>> desbanearUsuario(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Usuario moderador = usuarioService.buscarPorEmail(email);

            if (!moderador.getEsModerador()) {
                return ResponseEntity
                        .status(403)
                        .body(ApiResponse.error("No tienes permisos de moderador"));
            }

            usuarioService.banearUsuario(id, false, null);

            return ResponseEntity.ok(
                    ApiResponse.success(null, "Usuario desbaneado correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Actualizar estado de conexión
     */
    @PutMapping("/estado")
    public ResponseEntity<ApiResponse<Void>> actualizarEstadoConexion(
            @RequestParam Boolean conectado,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email);

            usuarioService.actualizarEstadoConexion(usuario.getId(), conectado);

            String mensaje = conectado ? "Conectado" : "Desconectado";
            return ResponseEntity.ok(
                    ApiResponse.success(null, mensaje)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}