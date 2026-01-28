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

import java.util.ArrayList;
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
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar que el nombre de usuario no exista
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Determinar si es moderador
        boolean esModerador = EMAILS_MODERADORES.contains(request.getEmail().toLowerCase()) ||
                (request.getCodigoModerador() != null &&
                        CODIGOS_MODERADOR.contains(request.getCodigoModerador()));

        // Crear usuario
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreUsuario(request.getNombreUsuario())
                .esModerador(esModerador)
                .estadoConexion(true)
                .build();

        usuarioRepository.save(usuario);

        // Generar token
        String token = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        usuario.getEmail(),
                        usuario.getPassword(),
                        new ArrayList<>()
                )
        );

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombreUsuario(usuario.getNombreUsuario())
                .esModerador(usuario.getEsModerador())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si está baneado
        if (usuario.getEstaBaneado()) {
            throw new RuntimeException("Tu cuenta ha sido suspendida: " + usuario.getRazonBaneo());
        }

        // Actualizar estado de conexión
        usuario.setEstadoConexion(true);
        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        usuario.getEmail(),
                        usuario.getPassword(),
                        new ArrayList<>()
                )
        );

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombreUsuario(usuario.getNombreUsuario())
                .esModerador(usuario.getEsModerador())
                .build();
    }
}
