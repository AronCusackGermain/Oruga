package com.oruga.backend.security;

import com.oruga.backend.model.Usuario;
import com.oruga.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio personalizado para cargar detalles de usuario
 * Implementa UserDetailsService de Spring Security
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email
                ));

        // Construir autoridades (roles)
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Si es moderador, agregar autoridad de MODERADOR
        if (usuario.getEsModerador()) {
            authorities.add(new SimpleGrantedAuthority("MODERADOR"));
        }

        // Todos tienen autoridad de USER
        authorities.add(new SimpleGrantedAuthority("USER"));

        // Verificar si el usuario está baneado
        boolean accountNonLocked = !usuario.getEstaBaneado();

        // Retornar UserDetails de Spring Security
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(authorities)
                .accountLocked(!accountNonLocked)
                .disabled(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}