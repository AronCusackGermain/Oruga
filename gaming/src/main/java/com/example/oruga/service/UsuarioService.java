package com.example.oruga.service;

import com.example.oruga.model.Usuario;
import com.example.oruga.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de Usuario - Lógica de negocio
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Registrar nuevo usuario
     */
    @Transactional
    public Usuario registrar(Usuario usuario) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar contraseña
        if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        // Establecer valores por defecto
        usuario.setFechaCreacion(System.currentTimeMillis());
        usuario.setUltimaConexion(System.currentTimeMillis());
        usuario.setEstadoConexion(false);

        return usuarioRepository.save(usuario);
    }

    /**
     * Login - autenticación
     */
    @Transactional
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe una cuenta con este email"));

        // Verificar si está baneado
        if (usuario.getEstaBaneado()) {
            throw new RuntimeException("Tu cuenta ha sido suspendida. Razón: " + usuario.getRazonBaneo());
        }

        // Verificar contraseña
        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("La contraseña es incorrecta");
        }

        // Actualizar estado de conexión
        usuarioRepository.actualizarEstadoConexion(usuario.getId(), true);
        usuarioRepository.actualizarUltimaConexion(usuario.getId(), System.currentTimeMillis());
        usuario.setEstadoConexion(true);
        usuario.setUltimaConexion(System.currentTimeMillis());

        return usuario;
    }

    /**
     * Logout
     */
    @Transactional
    public void logout(Integer usuarioId) {
        usuarioRepository.actualizarEstadoConexion(usuarioId, false);
    }

    /**
     * Obtener usuario por ID
     */
    public Optional<Usuario> obtenerPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Obtener todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtener usuarios conectados
     */
    public List<Usuario> obtenerConectados() {
        return usuarioRepository.findByEstadoConexion(true);
    }

    /**
     * Obtener usuarios baneados
     */
    public List<Usuario> obtenerBaneados() {
        return usuarioRepository.findByEstaBaneado(true);
    }

    /**
     * Actualizar usuario
     */
    @Transactional
    public Usuario actualizar(Usuario usuario) {
        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Banear usuario
     */
    @Transactional
    public void banearUsuario(Integer usuarioId, String razon) {
        usuarioRepository.banearUsuario(usuarioId, true, System.currentTimeMillis(), razon);
    }

    /**
     * Desbanear usuario
     */
    @Transactional
    public void desbanearUsuario(Integer usuarioId) {
        usuarioRepository.banearUsuario(usuarioId, false, null, "");
    }
}