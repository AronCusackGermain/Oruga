package com.example.oruga.service;

import com.example.oruga.model.Publicacion;
import com.example.oruga.repository.PublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de Publicacion
 */
@Service
public class PublicacionService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    /**
     * Crear publicación
     */
    @Transactional
    public Publicacion crear(Publicacion publicacion) {
        if (publicacion.getTitulo() == null || publicacion.getTitulo().isBlank()) {
            throw new RuntimeException("El título es obligatorio");
        }
        if (publicacion.getContenido() == null || publicacion.getContenido().isBlank()) {
            throw new RuntimeException("El contenido es obligatorio");
        }

        publicacion.setFechaPublicacion(System.currentTimeMillis());
        return publicacionRepository.save(publicacion);
    }

    /**
     * Obtener todas las publicaciones
     */
    public List<Publicacion> obtenerTodas() {
        return publicacionRepository.findAllByOrderByFechaPublicacionDesc();
    }

    /**
     * Obtener anuncios
     */
    public List<Publicacion> obtenerAnuncios() {
        return publicacionRepository.findByEsAnuncio(true);
    }

    /**
     * Obtener publicaciones por autor
     */
    public List<Publicacion> obtenerPorAutor(Integer autorId) {
        return publicacionRepository.findByAutorIdOrderByFechaPublicacionDesc(autorId);
    }

    /**
     * Obtener por ID
     */
    public Optional<Publicacion> obtenerPorId(Integer id) {
        return publicacionRepository.findById(id);
    }

    /**
     * Dar like
     */
    @Transactional
    public void darLike(Integer publicacionId) {
        publicacionRepository.incrementarLikes(publicacionId);
    }

    /**
     * Incrementar comentarios
     */
    @Transactional
    public void incrementarComentarios(Integer publicacionId) {
        publicacionRepository.incrementarComentarios(publicacionId);
    }

    /**
     * Eliminar publicación
     */
    @Transactional
    public void eliminar(Integer id) {
        publicacionRepository.deleteById(id);
    }
}