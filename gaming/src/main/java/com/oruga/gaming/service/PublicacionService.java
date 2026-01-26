package com.oruga.gaming.service;

import com.oruga.gaming.dto.request.PublicacionRequest;
import com.oruga.gaming.dto.response.PublicacionResponse;
import com.oruga.gaming.entity.Publicacion;
import com.oruga.gaming.exception.ResourceNotFoundException;
import com.oruga.gaming.repository.PublicacionRepository;
import com.oruga.gaming.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public PublicacionResponse crearPublicacion(Long autorId, String autorNombre,
                                                PublicacionRequest request, String imagenUrl) {
        Publicacion publicacion = Publicacion.builder()
                .autorId(autorId)
                .autorNombre(autorNombre)
                .titulo(request.getTitulo())
                .contenido(request.getContenido())
                .imagenUrl(imagenUrl)
                .esAnuncio(request.getEsAnuncio())
                .fechaPublicacion(LocalDateTime.now())
                .build();

        publicacion = publicacionRepository.save(publicacion);

        // Incrementar contador del usuario
        usuarioService.incrementarPublicaciones(autorId);

        return mapToResponse(publicacion);
    }

    public List<PublicacionResponse> obtenerTodasPublicaciones() {
        return publicacionRepository.findAllByOrderByFechaPublicacionDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PublicacionResponse> obtenerAnuncios() {
        return publicacionRepository.findByEsAnuncioTrueOrderByFechaPublicacionDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PublicacionResponse obtenerPorId(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", "id", id));
        return mapToResponse(publicacion);
    }

    public List<PublicacionResponse> obtenerPorAutor(Long autorId) {
        return publicacionRepository.findByAutorIdOrderByFechaPublicacionDesc(autorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void darLike(Long publicacionId) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", "id", publicacionId));
        publicacion.incrementarLikes();
        publicacionRepository.save(publicacion);
    }

    @Transactional
    public void incrementarComentarios(Long publicacionId) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", "id", publicacionId));
        publicacion.incrementarComentarios();
        publicacionRepository.save(publicacion);
    }

    @Transactional
    public void decrementarComentarios(Long publicacionId) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", "id", publicacionId));
        publicacion.decrementarComentarios();
        publicacionRepository.save(publicacion);
    }

    @Transactional
    public void eliminarPublicacion(Long publicacionId) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", "id", publicacionId));
        publicacionRepository.delete(publicacion);
    }

    private PublicacionResponse mapToResponse(Publicacion publicacion) {
        return PublicacionResponse.builder()
                .id(publicacion.getId())
                .autorId(publicacion.getAutorId())
                .autorNombre(publicacion.getAutorNombre())
                .titulo(publicacion.getTitulo())
                .contenido(publicacion.getContenido())
                .imagenUrl(publicacion.getImagenUrl())
                .fechaPublicacion(publicacion.getFechaPublicacion())
                .fechaPublicacionFormateada(DateUtils.formatearTiempoRelativo(publicacion.getFechaPublicacion()))
                .cantidadLikes(publicacion.getCantidadLikes())
                .cantidadComentarios(publicacion.getCantidadComentarios())
                .esAnuncio(publicacion.getEsAnuncio())
                .build();
    }
}