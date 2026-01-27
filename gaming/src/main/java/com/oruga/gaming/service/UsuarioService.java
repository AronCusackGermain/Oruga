package com.oruga.gaming.service;

import com.oruga.gaming.dto.RegisterRequest;
import com.oruga.gaming.dto.UsuarioDto;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Código de moderador (debería estar en application.properties)
    private static final String CODIGO_MODERADOR = "ORUGA2026MOD";
    private static final String CODIGO_DEMO = "DEMO123";

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(new ArrayList<>())
                .accountLocked(usuario.getEstaBaneado())
                .build();
    }

    public Usuario registrarUsuario(RegisterRequest request) {
        // Validar email único
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Determinar si es moderador
        boolean esModerador = validarCodigoModerador(request.getCodigoModerador());

        // Crear usuario
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreUsuario(request.getNombreUsuario())
                .esModerador(esModerador)
                .estaBaneado(false)
                .estadoConexion(false)
                .cantidadPublicaciones(0)
                .cantidadMensajes(0)
                .cantidadReportes(0)
                .build();

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void actualizarEstadoConexion(Long usuarioId, Boolean conectado) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.setEstadoConexion(conectado);
        usuario.setUltimaConexion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    public void banearUsuario(Long usuarioId, Boolean banear, String razon) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.setEstaBaneado(banear);
        if (banear) {
            usuario.setFechaBaneo(LocalDateTime.now());
            usuario.setRazonBaneo(razon);
        } else {
            usuario.setFechaBaneo(null);
            usuario.setRazonBaneo(null);
        }
        usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerUsuariosBaneados() {
        return usuarioRepository.findByEstaBaneado(true);
    }

    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> obtenerPorEstadoConexion(Boolean conectado) {
        return usuarioRepository.findByEstadoConexion(conectado);
    }

    public void actualizarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public void incrementarPublicaciones(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.setCantidadPublicaciones(usuario.getCantidadPublicaciones() + 1);
        usuarioRepository.save(usuario);
    }

    public void incrementarMensajes(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.setCantidadMensajes(usuario.getCantidadMensajes() + 1);
        usuarioRepository.save(usuario);
    }

    public UsuarioDto toDto(Usuario usuario) {
        return UsuarioDto.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombreUsuario(usuario.getNombreUsuario())
                .esModerador(usuario.getEsModerador())
                .estaBaneado(usuario.getEstaBaneado())
                .urlFotoPerfil(usuario.getUrlFotoPerfil())
                .descripcion(usuario.getDescripcion())
                .cantidadPublicaciones(usuario.getCantidadPublicaciones())
                .cantidadMensajes(usuario.getCantidadMensajes())
                .build();
    }

    private boolean validarCodigoModerador(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return false;
        }
        return CODIGO_MODERADOR.equals(codigo) || CODIGO_DEMO.equals(codigo);
    }
}