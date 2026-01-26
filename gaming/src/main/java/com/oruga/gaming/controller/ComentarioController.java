package com.oruga.gaming.controller;

import com.oruga.gaming.dto.request.ComentarioRequest;
import com.oruga.gaming.dto.response.ApiResponse;
import com.oruga.gaming.entity.Comentario;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.repository.UsuarioRepository;
import com.oruga.gaming.service.ComentarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para comentarios en publicaciones
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final UsuarioRepository usuarioRepository;

    /**
     * GET /api/publicaciones/{publicacionId}/comentarios
     * Obtiene todos los comentarios de una publicación
     */
    @GetMapping("/publicaciones/{publicacionId}/comentarios")
    public ResponseEntity<ApiResponse<List<Comentario>>> obtenerComentarios(
            @PathVariable Long publicacionId) {

        List<Comentario> comentarios = comentarioService.obtenerComentariosPorPublicacion(publicacionId);
        return ResponseEntity.ok(
                ApiResponse.success("Comentarios obtenidos exitosamente", comentarios)
        );
    }

    /**
     * POST /api/publicaciones/{publicacionId}/comentarios
     * Agrega un comentario a una publicación
     */
    @PostMapping("/publicaciones/{publicacionId}/comentarios")
    public ResponseEntity<ApiResponse<Comentario>> crearComentario(
            @PathVariable Long publicacionId,
            @Valid @RequestBody ComentarioRequest request,
            HttpServletRequest httpRequest) {

        Long usuarioId = (Long) httpRequest.getAttribute("usuarioId");

        // Obtener nombre del usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear comentario
        Comentario comentario = comentarioService.crearComentario(
                publicacionId,
                usuarioId,
                usuario.getNombreUsuario(),
                request
        );

        return ResponseEntity.ok(
                ApiResponse.success("Comentario creado exitosamente", comentario)
        );
    }

    /**
     * DELETE /api/comentarios/{id}
     * Elimina un comentario
     */
    @DeleteMapping("/comentarios/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarComentario(@PathVariable Long id) {
        comentarioService.eliminarComentario(id);
        return ResponseEntity.ok(
                ApiResponse.success("Comentario eliminado exitosamente", null)
        );
    }
}