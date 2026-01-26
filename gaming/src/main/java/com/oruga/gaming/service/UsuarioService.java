package com.oruga.gaming.service;

import com.oruga.gaming.dto.response.UsuarioResponse;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.exception.ResourceNotFoundException;
import com.oruga.gaming.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de usuarios
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene todos los usuarios
     */
    public List<UsuarioResponse> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por ID
     */
    public UsuarioResponse obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return mapToResponse(usuario);
    }

    /**
     * Actualiza el perfil del usuario
     */
    @Transactional
    public UsuarioResponse actualizarPerfil(Long usuarioId, String descripcion,
                                            String urlFotoPerfil, String steamId, String discordId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        if (descripcion != null) usuario.setDescripcion(descripcion);
        if (urlFotoPerfil != null) usuario.setUrlFotoPerfil(urlFotoPerfil);
        if (steamId != null) usuario.setSteamId(steamId);
        if (discordId != null) usuario.setDiscordId(discordId);

        usuario = usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

    /**
     * Actualiza el estado de conexión del usuario
     */
    @Transactional
    public void actualizarEstadoConexion(Long usuarioId, boolean conectado) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        usuario.setEstadoConexion(conectado);
        if (conectado) {
            usuario.setUltimaConexion(LocalDateTime.now());
        }
        usuarioRepository.save(usuario);
    }

    /**
     * Incrementa contador de publicaciones
     */
    @Transactional
    public void incrementarPublicaciones(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        usuario.setCantidadPublicaciones(usuario.getCantidadPublicaciones() + 1);
        usuarioRepository.save(usuario);
    }

    /**
     * Incrementa contador de mensajes
     */
    @Transactional
    public void incrementarMensajes(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        usuario.setCantidadMensajes(usuario.getCantidadMensajes() + 1);
        usuarioRepository.save(usuario);
    }

    /**
     * Convierte Usuario a UsuarioResponse
     */
    private UsuarioResponse mapToResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombreUsuario(usuario.getNombreUsuario())
                .esModerador(usuario.getEsModerador())
                .estaBaneado(usuario.getEstaBaneado())
                .urlFotoPerfil(usuario.getUrlFotoPerfil())
                .descripcion(usuario.getDescripcion())
                .steamId(usuario.getSteamId())
                .discordId(usuario.getDiscordId())
                .cantidadPublicaciones(usuario.getCantidadPublicaciones())
                .cantidadMensajes(usuario.getCantidadMensajes())
                .build();
    }
}