package com.oruga.gaming.service;

import com.oruga.gaming.dto.request.ComentarioRequest;
import com.oruga.gaming.entity.Comentario;
import com.oruga.gaming.exception.ResourceNotFoundException;
import com.oruga.gaming.repository.ComentarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final PublicacionService publicacionService;

    @Transactional
    public Comentario crearComentario(Long publicacionId, Long autorId,
                                      String autorNombre, ComentarioRequest request) {
        Comentario comentario = Comentario.builder()
                .publicacionId(publicacionId)
                .autorId(autorId)
                .autorNombre(autorNombre)
                .contenido(request.getContenido())
                .fechaComentario(LocalDateTime.now())
                .build();

        comentario = comentarioRepository.save(comentario);

        // Incrementar contador en la publicación
        publicacionService.incrementarComentarios(publicacionId);

        return comentario;
    }

    public List<Comentario> obtenerComentariosPorPublicacion(Long publicacionId) {
        return comentarioRepository.findByPublicacionIdOrderByFechaComentarioDesc(publicacionId);
    }

    @Transactional
    public void eliminarComentario(Long comentarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario", "id", comentarioId));

        Long publicacionId = comentario.getPublicacionId();
        comentarioRepository.delete(comentario);

        // Decrementar contador en la publicación
        publicacionService.decrementarComentarios(publicacionId);
    }
}