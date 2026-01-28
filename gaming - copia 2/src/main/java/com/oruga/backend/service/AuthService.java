package com.oruga.backend.service;

import com.oruga.backend.dto.response.AuthResponse;
import com.oruga.backend.dto.request.LoginRequest;
import com.oruga.backend.dto.request.RegisterRequest;
import com.oruga.backend.model.Usuario;
import com.oruga.backend.repository.UsuarioRepository;
import com.oruga.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final List<String> EMAILS_MODERADORES = Arrays.asList(
            "tu-email@duocuc.cl",
            "compañero-email@duocuc.cl",
            "vpobletel@profesor.duoc.cl",
            "moderador@oruga.com"
    );

    private static final List<String> CODIGOS_MODERADOR = Arrays.asList(
            "ORUGA2026MOD",
            "DEMO123"
    );

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        // 1. Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // 2. Validar que el nombre de usuario no exista
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // 3. Determinar si es moderador
        boolean esModerador = EMAILS_MODERADORES.contains(request.getEmail().toLowerCase()) ||
                (request.getCodigoModerador() != null &&
                        CODIGOS_MODERADOR.contains(request.getCodigoModerador()));

        // 4. Crear usuario
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreUsuario(request.getNombreUsuario())
                .esModerador(esModerador)
                .estadoConexion(true)
                .build();

        // 5. Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 6. Asegurar valores por defecto (por si @PrePersist no se ejecutó)
        if (usuarioGuardado.getCantidadMensajes() == null) {
            usuarioGuardado.setCantidadMensajes(0);
        }
        if (usuarioGuardado.getCantidadPublicaciones() == null) {
            usuarioGuardado.setCantidadPublicaciones(0);
        }
        if (usuarioGuardado.getCantidadReportes() == null) {
            usuarioGuardado.setCantidadReportes(0);
        }

        // 7. ✅ GENERAR TOKEN CON USUARIO COMPLETO
        String token = jwtService.generateToken(usuarioGuardado);

        // 8. Retornar respuesta
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuarioGuardado.getId())
                .email(usuarioGuardado.getEmail())
                .nombreUsuario(usuarioGuardado.getNombreUsuario())
                .esModerador(usuarioGuardado.getEsModerador())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Obtener usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Verificar si está baneado
        if (usuario.getEstaBaneado()) {
            throw new RuntimeException("Tu cuenta ha sido suspendida: " + usuario.getRazonBaneo());
        }

        // 4. Actualizar estado de conexión
        usuario.setEstadoConexion(true);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        // 5. ✅ GENERAR TOKEN CON USUARIO COMPLETO
        String token = jwtService.generateToken(usuarioActualizado);

        // 6. Retornar respuesta
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuarioActualizado.getId())
                .email(usuarioActualizado.getEmail())
                .nombreUsuario(usuarioActualizado.getNombreUsuario())
                .esModerador(usuarioActualizado.getEsModerador())
                .build();
    }
}
