package com.oruga.gaming.controller;

import com.oruga.gaming.dto.response.ApiResponse;
import com.oruga.gaming.dto.response.JuegoResponse;
import com.oruga.gaming.service.JuegoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión del catálogo de juegos
 */
@RestController
@RequestMapping("/api/juegos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JuegoController {

    private final JuegoService juegoService;

    /**
     * GET /api/juegos
     * Obtiene todos los juegos activos con precios en CLP
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<JuegoResponse>>> obtenerTodosLosJuegos() {
        List<JuegoResponse> juegos = juegoService.obtenerTodosLosJuegos();
        return ResponseEntity.ok(
                ApiResponse.success("Juegos obtenidos exitosamente", juegos)
        );
    }

    /**
     * GET /api/juegos/{id}
     * Obtiene el detalle de un juego específico
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JuegoResponse>> obtenerJuegoPorId(@PathVariable Long id) {
        JuegoResponse juego = juegoService.obtenerJuegoPorId(id);
        return ResponseEntity.ok(
                ApiResponse.success("Juego obtenido exitosamente", juego)
        );
    }

    /**
     * GET /api/juegos/buscar?genero=RPG
     * Busca juegos por género
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<JuegoResponse>>> buscarPorGenero(
            @RequestParam String genero) {

        List<JuegoResponse> juegos = juegoService.buscarPorGenero(genero);
        return ResponseEntity.ok(
                ApiResponse.success("Búsqueda completada", juegos)
        );
    }
}