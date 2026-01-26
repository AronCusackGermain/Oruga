package com.oruga.gaming.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad con Spring Security + JWT
 * Define qué rutas son públicas y cuáles requieren autenticación
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class Securityconfig {

    private final Jwtauthenticationfilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (no es necesario para APIs REST stateless)
                .csrf(csrf -> csrf.disable())

                // Configurar autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (sin autenticación)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/juegos/**").permitAll()
                        .requestMatchers("/api/publicaciones/*/comentarios").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Rutas que requieren autenticación (moderadores)
                        .requestMatchers("/api/moderacion/**").hasAuthority("MODERADOR")

                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated()
                )

                // Configurar sesiones como STATELESS (sin estado, usando JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Agregar filtro JWT antes del filtro de autenticación de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean para encriptar contraseñas con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}