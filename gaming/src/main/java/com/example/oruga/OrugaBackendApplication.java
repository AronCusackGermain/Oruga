package com.example.oruga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal del backend Oruga
 */
@SpringBootApplication
public class OrugaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrugaBackendApplication.class, args);
        System.out.println("=================================");
        System.out.println("🐛 ORUGA BACKEND INICIADO");
        System.out.println("Puerto: 8080");
        System.out.println("API Base: http://localhost:8080/api");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("=================================");
    }
}