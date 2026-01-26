package com.oruga.gaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Aplicación principal de Oruga Gaming API
 * Backend desarrollado con Spring Boot + MySQL
 * 
 * @author Equipo Oruga
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class GamingApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamingApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════╗
            ║   🐛 ORUGA GAMING API - INICIADA             ║
            ║   Spring Boot + MySQL + JWT                  ║
            ║                                               ║
            ║   ✅ Chat Privado                            ║
            ║   ✅ Comentarios                             ║
            ║   ✅ Upload de Imágenes                      ║
            ║   ✅ Carrito (CLP)                           ║
            ║   ✅ Sistema de Pago                         ║
            ╚═══════════════════════════════════════════════╝
            
            🚀 API lista para recibir peticiones
            📊 Base de datos: MySQL
            🔐 Autenticación: JWT
            
            """);
    }

}
