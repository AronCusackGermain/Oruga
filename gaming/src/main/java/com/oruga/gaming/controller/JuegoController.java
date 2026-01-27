package com.oruga.gaming.controller;

import com.oruga.gaming.dto.*;
import com.oruga.gaming.service.JuegoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/juegos")
@CrossOrigin(origins = "*")
public class JuegoController {

    private final JuegoService juegoService;

    public JuegoController(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JuegoDto>>> obtenerTodos() {
        List<JuegoDto> juegos = juegoService.obtenerTodos();
        return ResponseEntity.ok(
                ApiResponse.success(juegos, "Juegos obtenidos correctamente")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JuegoDto>> obtenerPorId(@PathVariable Long id) {
        try {
            JuegoDto juego = juegoService.obtenerPorId(id);
            return ResponseEntity.ok(
                    ApiResponse.success(juego, "Juego encontrado")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Juego no encontrado"));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<JuegoDto>>> buscarPorGenero(
            @RequestParam String genero) {
        List<JuegoDto> juegos = juegoService.buscarPorGenero(genero);
        return ResponseEntity.ok(
                ApiResponse.success(juegos, "Búsqueda completada")
        );
    }
}

// ========== CARRITO CONTROLLER ==========

