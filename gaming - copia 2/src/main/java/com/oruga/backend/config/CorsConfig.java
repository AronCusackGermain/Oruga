package com.oruga.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing)
 * Permite que el frontend (Android/Web) acceda a la API desde diferentes orígenes
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // DESARROLLO: Permite todos los orígenes
        // PRODUCCIÓN: Cambiar a dominios específicos
        configuration.setAllowedOrigins(List.of("*"));

        // Alternativamente, para producción usa:
        // configuration.setAllowedOrigins(Arrays.asList(
        //     "https://tu-app.up.railway.app",
        //     "https://tu-dominio.com"
        // ));

        // Permitir todos los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));

        // Permitir todos los headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Exponer headers específicos al frontend
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        // Permitir envío de credenciales (cookies, headers de autenticación)
        // NOTA: Si allowCredentials es true, NO puedes usar allowedOrigins("*")
        // Tendrías que especificar los orígenes exactos
        configuration.setAllowCredentials(false);

        // Tiempo de caché de la respuesta preflight (en segundos)
        configuration.setMaxAge(3600L); // 1 hora

        // Aplicar configuración a todos los endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
