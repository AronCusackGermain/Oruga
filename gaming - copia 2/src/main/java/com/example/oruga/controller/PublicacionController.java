package com.example.oruga.controller;

import com.example.oruga.model.Publicacion;
import com.example.oruga.service.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para Publicacion
 * Base URL: /api/publicaciones
 */
@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "*")
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    /**
     * POST /api/publicaciones
     * Crear nueva publicación
     */
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Publicacion publicacion) {
        try {
            Publicacion nueva = publicacionService.crear(publicacion);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Publicación creada");
            response.put("publicacion", nueva);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/publicaciones
     * Obtener todas las publicaciones
     */
    @GetMapping
    public ResponseEntity<List<Publicacion>> obtenerTodas() {
        List<Publicacion> publicaciones = publicacionService.obtenerTodas();
        return ResponseEntity.ok(publicaciones);
    }

    /**
     * GET /api/publicaciones/anuncios
     * Obtener solo anuncios
     */
    @GetMapping("/anuncios")
    public ResponseEntity<List<Publicacion>> obtenerAnuncios() {
        List<Publicacion> anuncios = publicacionService.obtenerAnuncios();
        return ResponseEntity.ok(anuncios);
    }

    /**
     * GET /api/publicaciones/autor/{autorId}
     * Obtener publicaciones de un autor
     */
    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<Publicacion>> obtenerPorAutor(@PathVariable Integer autorId) {
        List<Publicacion> publicaciones = publicacionService.obtenerPorAutor(autorId);
        return ResponseEntity.ok(publicaciones);
    }

    /**
     * GET /api/publicaciones/{id}
     * Obtener publicación por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        return publicacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/publicaciones/{id}/like
     * Dar like a una publicación
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<?> darLike(@PathVariable Integer id) {
        try {
            publicacionService.darLike(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Like agregado");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * DELETE /api/publicaciones/{id}
     * Eliminar publicación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            publicacionService.eliminar(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Publicación eliminada");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}