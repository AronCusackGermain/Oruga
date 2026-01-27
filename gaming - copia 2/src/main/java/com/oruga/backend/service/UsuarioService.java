package com.oruga.backend.service;

import com.oruga.backend.dto.request.ActualizarPerfilRequest;
import com.oruga.backend.dto.response.UsuarioResponse;
import com.oruga.backend.model.Usuario;
import com.oruga.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar operaciones de Usuario
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtener perfil del usuario autenticado
     */
    public UsuarioResponse obtenerPerfilActual(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        return mapearAResponse(usuario);
    }

    /**
     * Obtener usuario por ID
     */
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapearAResponse(usuario);
    }

    /**
     * Obtener todos los usuarios
     */
    public List<UsuarioResponse> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuarios por estado de conexión
     */
    public List<UsuarioResponse> obtenerPorEstadoConexion(Boolean conectado) {
        return usuarioRepository.findByEstadoConexion(conectado)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuarios baneados
     */
    public List<UsuarioResponse> obtenerUsuariosBaneados() {
        return usuarioRepository.findByEstaBaneado(true)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener moderadores
     */
    public List<UsuarioResponse> obtenerModeradores() {
        return usuarioRepository.findByEsModerador(true)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar perfil del usuario
     */
    @Transactional
    public UsuarioResponse actualizarPerfil(
            ActualizarPerfilRequest request,
            Authentication auth
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);

        if (request.getDescripcion() != null) {
            usuario.setDescripcion(request.getDescripcion());
        }
        if (request.getUrlFotoPerfil() != null) {
            usuario.setUrlFotoPerfil(request.getUrlFotoPerfil());
        }

        usuarioRepository.save(usuario);
        return mapearAResponse(usuario);
    }

    /**
     * Conectar cuentas externas (Steam, Discord)
     */
    @Transactional
    public UsuarioResponse conectarCuentasExternas(
            String steamId,
            String discordId,
            Authentication auth
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);

        if (steamId != null) {
            usuario.setSteamId(steamId);
        }
        if (discordId != null) {
            usuario.setDiscordId(discordId);
        }

        usuarioRepository.save(usuario);
        return mapearAResponse(usuario);
    }

    /**
     * Actualizar estado de conexión
     */
    @Transactional
    public void actualizarEstadoConexion(Long usuarioId, Boolean conectado) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstadoConexion(conectado);
        usuarioRepository.save(usuario);
    }

    /**
     * Cerrar sesión (actualizar estado)
     */
    @Transactional
    public void logout(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        usuario.setEstadoConexion(false);
        usuarioRepository.save(usuario);
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Obtener usuario autenticado desde el token
     */
    private Usuario obtenerUsuarioAutenticado(Authentication auth) {
        String email = auth.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Mapear Usuario a UsuarioResponse
     */
    private UsuarioResponse mapearAResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombreUsuario(usuario.getNombreUsuario())
                .esModerador(usuario.getEsModerador())
                .estaBaneado(usuario.getEstaBaneado())
                .fechaBaneo(usuario.getFechaBaneo())
                .razonBaneo(usuario.getRazonBaneo())
                .urlFotoPerfil(usuario.getUrlFotoPerfil())
                .descripcion(usuario.getDescripcion())
                .steamId(usuario.getSteamId())
                .discordId(usuario.getDiscordId())
                .fechaCreacion(usuario.getFechaCreacion())
                .ultimaConexion(usuario.getUltimaConexion())
                .estadoConexion(usuario.getEstadoConexion())
                .cantidadPublicaciones(usuario.getCantidadPublicaciones())
                .cantidadMensajes(usuario.getCantidadMensajes())
                .cantidadReportes(usuario.getCantidadReportes())
                .build();
    }
}