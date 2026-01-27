package com.oruga.gaming.controller;

import com.oruga.gaming.dto.ApiResponse;
import com.oruga.gaming.dto.PublicacionDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.oruga.gaming.service.PublicacionService;

import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "*")
public class PublicacionController {

    private final PublicacionService publicacionService;

    public PublicacionController(PublicacionService publicacionService) {
        this.publicacionService = publicacionService;
    }

    /**
     * Crear una nueva publicación
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PublicacionDto>> crearPublicacion(
            @Valid @RequestBody PublicacionRequest request,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            PublicacionDto publicacion = publicacionService.crearPublicacion(
                    email,
                    request.getTitulo(),
                    request.getContenido(),
                    request.getEsAnuncio()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(publicacion, "Publicación creada correctamente"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error al crear publicación: " + e.getMessage()));
        }
    }

    /**
     * Obtener todas las publicaciones
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PublicacionDto>>> obtenerTodasPublicaciones() {
        try {
            List<PublicacionDto> publicaciones = publicacionService.obtenerTodasPublicaciones();
            return ResponseEntity.ok(
                    ApiResponse.success(publicaciones, "Publicaciones obtenidas correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener solo anuncios
     */
    @GetMapping("/anuncios")
    public ResponseEntity<ApiResponse<List<PublicacionDto>>> obtenerAnuncios() {
        try {
            List<PublicacionDto> anuncios = publicacionService.obtenerAnuncios();
            return ResponseEntity.ok(
                    ApiResponse.success(anuncios, "Anuncios obtenidos correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener publicación por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicacionDto>> obtenerPublicacionPorId(
            @PathVariable Long id) {

        try {
            PublicacionDto publicacion = publicacionService.obtenerPorId(id);
            return ResponseEntity.ok(
                    ApiResponse.success(publicacion, "Publicación encontrada")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Publicación no encontrada"));
        }
    }

    /**
     * Obtener publicaciones por autor
     */
    @GetMapping("/autor/{autorId}")
    public ResponseEntity<ApiResponse<List<PublicacionDto>>> obtenerPublicacionesPorAutor(
            @PathVariable Long autorId) {

        try {
            List<PublicacionDto> publicaciones =
                    publicacionService.obtenerPublicacionesPorAutor(autorId);
            return ResponseEntity.ok(
                    ApiResponse.success(publicaciones, "Publicaciones del autor obtenidas")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Dar like a una publicación
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> darLike(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            publicacionService.darLike(id);
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Like registrado")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Actualizar publicación
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicacionDto>> actualizarPublicacion(
            @PathVariable Long id,
            @Valid @RequestBody PublicacionUpdateRequest request,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            PublicacionDto publicacion = publicacionService.actualizarPublicacion(
                    email, id, request.getTitulo(), request.getContenido()
            );

            return ResponseEntity.ok(
                    ApiResponse.success(publicacion, "Publicación actualizada")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Eliminar publicación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPublicacion(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            publicacionService.eliminarPublicacion(email, id);

            return ResponseEntity.ok(
                    ApiResponse.success(null, "Publicación eliminada correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    // ========== DTOs Internos ==========

    /**
     * Request para crear publicación
     */
    public static class PublicacionRequest {
        private String titulo;
        private String contenido;
        private Boolean esAnuncio = false;

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getContenido() {
            return contenido;
        }

        public void setContenido(String contenido) {
            this.contenido = contenido;
        }

        public Boolean getEsAnuncio() {
            return esAnuncio;
        }

        public void setEsAnuncio(Boolean esAnuncio) {
            this.esAnuncio = esAnuncio;
        }
    }

    /**
     * Request para actualizar publicación
     */
    public static class PublicacionUpdateRequest {
        private String titulo;
        private String contenido;

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getContenido() {
            return contenido;
        }

        public void setContenido(String contenido) {
            this.contenido = contenido;
        }
    }
}