package com.oruga.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Clase principal de la aplicación Oruga Backend
 * Foro de Videojuegos - API REST
 *
 * @author Oruga Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class OrugaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrugaBackendApplication.class, args);

        System.out.println("\n" +
                "╔═══════════════════════════════════════╗\n" +
                "║   🐛 ORUGA BACKEND INICIADO 🐛       ║\n" +
                "║   Foro de Videojuegos                 ║\n" +
                "║   Puerto: 8080                        ║\n" +
                "║   Swagger: /swagger-ui.html           ║\n" +
                "╚═══════════════════════════════════════╝\n"
        );
    }
}