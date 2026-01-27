package com.oruga.backend.service;

import com.oruga.backend.dto.request.CrearPublicacionRequest;
import com.oruga.backend.dto.response.PublicacionResponse;
import com.oruga.backend.model.Publicacion;
import com.oruga.backend.model.Usuario;
import com.oruga.backend.repository.PublicacionRepository;
import com.oruga.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PublicacionResponse crearPublicacion(CrearPublicacionRequest request, Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);

        Publicacion publicacion = Publicacion.builder()
                .autor(usuario)
                .autorNombre(usuario.getNombreUsuario())
                .titulo(request.getTitulo())
                .contenido(request.getContenido())
                .imagenesUrls(request.getImagenesUrls() != null ? request.getImagenesUrls() : "")
                .esAnuncio(usuario.getEsModerador() && request.getEsAnuncio())
                .build();

        publicacionRepository.save(publicacion);
        usuario.setCantidadPublicaciones(usuario.getCantidadPublicaciones() + 1);
        usuarioRepository.save(usuario);

        return mapearAResponse(publicacion);
    }

    public List<PublicacionResponse> obtenerTodasPublicaciones() {
        return publicacionRepository.findAllByOrderByFechaPublicacionDesc()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public PublicacionResponse obtenerPorId(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
        return mapearAResponse(publicacion);
    }

    @Transactional
    public void darLike(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
        publicacion.incrementarLikes();
        publicacionRepository.save(publicacion);
    }

    @Transactional
    public void eliminarPublicacion(Long id, Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        if (!publicacion.getAutor().getId().equals(usuario.getId()) && !usuario.puedeModerar()) {
            throw new RuntimeException("No tienes permiso para eliminar esta publicación");
        }

        publicacionRepository.delete(publicacion);
    }

    private Usuario obtenerUsuarioAutenticado(Authentication auth) {
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private PublicacionResponse mapearAResponse(Publicacion publicacion) {
        return PublicacionResponse.builder()
                .id(publicacion.getId())
                .autorNombre(publicacion.getAutorNombre())
                .titulo(publicacion.getTitulo())
                .contenido(publicacion.getContenido())
                .imagenUrl(publicacion.getImagenUrl())
                .imagenesUrls(publicacion.getImagenesUrls())
                .fechaPublicacion(publicacion.getFechaPublicacion())
                .cantidadLikes(publicacion.getCantidadLikes())
                .cantidadComentarios(publicacion.getCantidadComentarios())
                .esAnuncio(publicacion.getEsAnuncio())
                .build();
    }
}