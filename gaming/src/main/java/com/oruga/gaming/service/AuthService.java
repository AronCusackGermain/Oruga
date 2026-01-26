package com.oruga.gaming.service;

import com.oruga.gaming.dto.request.LoginRequest;
import com.oruga.gaming.dto.request.RegisterRequest;
import com.oruga.gaming.dto.response.JwtResponse;
import com.oruga.gaming.dto.response.UsuarioResponse;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.exception.BadRequestException;
import com.oruga.gaming.exception.UnauthorizedException;
import com.oruga.gaming.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Servicio de autenticación - Maneja registro y login de usuarios
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${moderador.emails}")
    private String moderadorEmailsStr;

    @Value("${moderador.code1}")
    private String moderadorCode1;

    @Value("${moderador.code2}")
    private String moderadorCode2;

    /**
     * Registra un nuevo usuario
     */
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        // Determinar si es moderador
        boolean esModerador = esEmailModerador(request.getEmail()) ||
                validarCodigoModerador(request.getCodigoModerador());

        // Crear usuario
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreUsuario(request.getNombreUsuario())
                .esModerador(esModerador)
                .fechaCreacion(LocalDateTime.now())
                .ultimaConexion(LocalDateTime.now())
                .estadoConexion(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        // Generar token JWT
        String token = jwtService.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getEsModerador()
        );

        // Construir respuesta
        UsuarioResponse usuarioResponse = mapToUsuarioResponse(usuario);

        return JwtResponse.builder()
                .token(token)
                .usuario(usuarioResponse)
                .build();
    }

    /**
     * Autentica un usuario y devuelve token JWT
     */
    @Transactional
    public JwtResponse login(LoginRequest request) {
        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        // Validar que no esté baneado
        if (usuario.getEstaBaneado() != null && usuario.getEstaBaneado()) {
            String mensaje = "Tu cuenta ha sido suspendida";
            if (usuario.getRazonBaneo() != null && !usuario.getRazonBaneo().isEmpty()) {
                mensaje += ". Razón: " + usuario.getRazonBaneo();
            }
            throw new UnauthorizedException(mensaje);
        }

        // Validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // Actualizar última conexión
        usuario.setUltimaConexion(LocalDateTime.now());
        usuario.setEstadoConexion(true);
        usuarioRepository.save(usuario);

        // Generar token JWT
        String token = jwtService.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getEsModerador()
        );

        // Construir respuesta
        UsuarioResponse usuarioResponse = mapToUsuarioResponse(usuario);

        return JwtResponse.builder()
                .token(token)
                .usuario(usuarioResponse)
                .build();
    }

    /**
     * Cierra sesión del usuario
     */
    @Transactional
    public void logout(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        usuario.setEstadoConexion(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Verifica si un email está en la lista de moderadores automáticos
     */
    private boolean esEmailModerador(String email) {
        if (moderadorEmailsStr == null || moderadorEmailsStr.isEmpty()) {
            return false;
        }
        List<String> moderadorEmails = Arrays.asList(moderadorEmailsStr.split(","));
        return moderadorEmails.stream()
                .map(String::trim)
                .anyMatch(e -> e.equalsIgnoreCase(email));
    }

    /**
     * Valida un código de moderador
     */
    private boolean validarCodigoModerador(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return false;
        }
        return codigo.equals(moderadorCode1) || codigo.equals(moderadorCode2);
    }

    /**
     * Convierte Usuario a UsuarioResponse
     */
    private UsuarioResponse mapToUsuarioResponse(Usuario usuario) {
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