package com.oruga.gaming.config;

import com.oruga.gaming.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro de autenticación JWT
 * Intercepta todas las peticiones HTTP y valida el token JWT
 */
@Component
@RequiredArgsConstructor
public class Jwtauthenticationfilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Obtener header Authorization
        String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraer token (remover "Bearer ")
            String jwt = authHeader.substring(7);

            // Validar token
            if (jwtService.validateToken(jwt) && !jwtService.isTokenExpired(jwt)) {

                // Extraer datos del token
                String email = jwtService.extractEmail(jwt);
                Long usuarioId = jwtService.extractUsuarioId(jwt);
                Boolean esModerador = jwtService.extractEsModerador(jwt);

                // Crear UserDetails
                UserDetails userDetails = User.builder()
                        .username(email)
                        .password("") // No necesitamos password aquí
                        .authorities(esModerador ?
                                Collections.singletonList(new SimpleGrantedAuthority("MODERADOR")) :
                                Collections.emptyList())
                        .build();

                // Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Agregar detalles de la request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Guardar usuarioId como atributo para usarlo en los controllers
                request.setAttribute("usuarioId", usuarioId);
                request.setAttribute("esModerador", esModerador);

                // Establecer autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Token inválido o expirado - no hacer nada, la request no estará autenticada
            logger.error("Error al procesar token JWT: " + e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}