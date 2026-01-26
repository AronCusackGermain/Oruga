package com.oruga.gaming.controller;

import com.oruga.gaming.dto.request.PublicacionRequest;
import com.oruga.gaming.dto.response.ApiResponse;
import com.oruga.gaming.dto.response.PublicacionResponse;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.repository.UsuarioRepository;
import com.oruga.gaming.service.FileStorageService;
import com.oruga.gaming.service.PublicacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller para publicaciones del foro
 */
@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final FileStorageService fileStorageService;
    private final UsuarioRepository usuarioRepository;

    /**
     * GET /api/publicaciones
     * Obtiene todas las publicaciones ordenadas por fecha
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PublicacionResponse>>> obtenerTodasPublicaciones() {
        List<PublicacionResponse> publicaciones = publicacionService.obtenerTodasPublicaciones();
        return ResponseEntity.ok(
                ApiResponse.success("Publicaciones obtenidas exitosamente", publicaciones)
        );
    }

    /**
     * GET /api/publicaciones/{id}
     * Obtiene el detalle de una publicación
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicacionResponse>> obtenerPublicacionPorId(
            @PathVariable Long id) {

        PublicacionResponse publicacion = publicacionService.obtenerPorId(id);
        return ResponseEntity.ok(
                ApiResponse.success("Publicación obtenida exitosamente", publicacion)
        );
    }

    /**
     * POST /api/publicaciones
     * Crea una nueva publicación (con imagen opcional)
     * Content-Type: multipart/form-data
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<PublicacionResponse>> crearPublicacion(
            @RequestParam("titulo") String titulo,
            @RequestParam("contenido") String contenido,
            @RequestParam(value = "esAnuncio", defaultValue = "false") Boolean esAnuncio,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            HttpServletRequest request) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");

        // Obtener nombre del usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Guardar imagen si existe
        String imagenUrl = null;
        if (imagen != null && !imagen.isEmpty()) {
            imagenUrl = fileStorageService.guardarImagenPublicacion(imagen);
        }

        // Crear request
        PublicacionRequest publicacionRequest = new PublicacionRequest();
        publicacionRequest.setTitulo(titulo);
        publicacionRequest.setContenido(contenido);
        publicacionRequest.setEsAnuncio(esAnuncio);

        // Crear publicación
        PublicacionResponse publicacion = publicacionService.crearPublicacion(
                usuarioId,
                usuario.getNombreUsuario(),
                publicacionRequest,
                imagenUrl
        );

        return ResponseEntity.ok(
                ApiResponse.success("Publicación creada exitosamente", publicacion)
        );
    }

    /**
     * POST /api/publicaciones/{id}/like
     * Da like a una publicación
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> darLike(@PathVariable Long id) {
        publicacionService.darLike(id);
        return ResponseEntity.ok(
                ApiResponse.success("Like agregado exitosamente", null)
        );
    }

    /**
     * DELETE /api/publicaciones/{id}
     * Elimina una publicación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPublicacion(@PathVariable Long id) {
        publicacionService.eliminarPublicacion(id);
        return ResponseEntity.ok(
                ApiResponse.success("Publicación eliminada exitosamente", null)
        );
    }

    /**
     * GET /api/publicaciones/usuario/{usuarioId}
     * Obtiene publicaciones de un usuario específico
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<PublicacionResponse>>> obtenerPublicacionesPorUsuario(
            @PathVariable Long usuarioId) {

        List<PublicacionResponse> publicaciones = publicacionService.obtenerPorAutor(usuarioId);
        return ResponseEntity.ok(
                ApiResponse.success("Publicaciones del usuario obtenidas exitosamente", publicaciones)
        );
    }

    /**
     * GET /api/publicaciones/anuncios
     * Obtiene solo los anuncios (publicaciones destacadas)
     */
    @GetMapping("/anuncios")
    public ResponseEntity<ApiResponse<List<PublicacionResponse>>> obtenerAnuncios() {
        List<PublicacionResponse> anuncios = publicacionService.obtenerAnuncios();
        return ResponseEntity.ok(
                ApiResponse.success("Anuncios obtenidos exitosamente", anuncios)
        );
    }
}